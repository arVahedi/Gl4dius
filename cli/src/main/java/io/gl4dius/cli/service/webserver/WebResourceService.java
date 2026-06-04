package io.gl4dius.cli.service.webserver;

import io.gl4dius.cli.exception.StaticResourceException;
import io.gl4dius.cli.model.dto.StaticResource;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebResourceService {

    private static final String TEMPLATE_RESOURCE_DIRECTORY_NAME = "resources";

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "html",
            "css",
            "js",
            "png",
            "jpg",
            "jpeg",
            "gif",
            "svg",
            "webp",
            "ico",
            "woff",
            "woff2",
            "ttf",
            "map"
    );

    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry("html", "text/html; charset=UTF-8"),
            Map.entry("css", "text/css; charset=UTF-8"),
            Map.entry("js", "application/javascript; charset=UTF-8"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("webp", "image/webp"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("woff", "font/woff"),
            Map.entry("woff2", "font/woff2"),
            Map.entry("ttf", "font/ttf"),
            Map.entry("map", "application/json; charset=UTF-8")
    );

    public Mono<Void> serve(@NonNull HttpServerRequest request,
                            @NonNull HttpServerResponse response,
                            @NonNull String templatePath) {

        if (!request.method().name().equals("GET") && !request.method().name().equals("HEAD")) {
            return response.status(HttpResponseStatus.METHOD_NOT_ALLOWED).send();
        }

        return Mono.fromCallable(() -> resolveRequestedFile(request, templatePath))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(resource -> writeResource(response, request, resource))
                .onErrorResume(StaticResourceException.class, ex ->
                        response.status(ex.getStatus()).send())
                .onErrorResume(ex -> response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR).send());
    }

    private @NonNull StaticResource resolveRequestedFile(@NonNull HttpServerRequest request, @NonNull String templatePath) throws IOException {
        var templateResourcePath = getTemplateResourceDirectoryPath(templatePath);
        String requestedPath = request.path();
        if (requestedPath.isBlank()) {
            throw new StaticResourceException(HttpResponseStatus.NOT_FOUND, "Not found");
        }
        if (requestedPath.startsWith("/resources/")) {
            requestedPath = requestedPath.replaceFirst("/resources/", "/");
        }
        requestedPath = decodePath(requestedPath);
        if (containsSuspiciousPathSequence(requestedPath)) {
            throw new StaticResourceException(HttpResponseStatus.FORBIDDEN, "Forbidden");
        }

        Path candidate = templateResourcePath.resolve(requestedPath).normalize();
        if (!candidate.startsWith(templateResourcePath)) {
            throw new StaticResourceException(HttpResponseStatus.NOT_FOUND, "Not found");
        }

        if (!isAllowedFile(candidate, templatePath)) {
            throw new StaticResourceException(HttpResponseStatus.FORBIDDEN, "Forbidden");
        }

        String extension = extensionOf(candidate);
        String contentType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        return new StaticResource(candidate, Files.size(candidate), contentType);
    }

    private @NonNull Path getTemplateResourceDirectoryPath(@NonNull String templatePath) {
        Path templateResourcePath = Path.of(templatePath)
                .getParent()
                .resolve(TEMPLATE_RESOURCE_DIRECTORY_NAME)
                .toAbsolutePath()
                .normalize();
        if (!Files.exists(templateResourcePath, LinkOption.NOFOLLOW_LINKS) || !Files.isDirectory(templateResourcePath, LinkOption.NOFOLLOW_LINKS)) {
            throw new StaticResourceException(HttpResponseStatus.NOT_FOUND, "Template root does not exist or is not a directory: " + templateResourcePath);
        }
        return templateResourcePath;
    }

    private @NonNull String decodePath(String path) {
        String decoded = URLDecoder.decode(path, StandardCharsets.UTF_8);

        /*
         * Reject double-encoded traversal tricks.
         *
         * Example:
         *   %252e%252e%252f
         * first decode -> %2e%2e%2f
         * second decode -> ../
         */
        String decodedAgain = URLDecoder.decode(decoded, StandardCharsets.UTF_8);
        if (!decoded.equals(decodedAgain)) {
            throw new StaticResourceException(HttpResponseStatus.FORBIDDEN, "Double-encoded path is not allowed");
        }

        return decoded;
    }

    private boolean containsSuspiciousPathSequence(@NonNull String path) {
        String normalized = path.replace('\\', '/');

        return normalized.contains("../")
                || normalized.contains("/..")
                || normalized.equals("..")
                || normalized.startsWith("/")
                || normalized.contains("\0");
    }

    private boolean isAllowedFile(Path file, @NonNull String templatePath) throws IOException {
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            return false;
        }

        if (Files.isSymbolicLink(file)) {
            return false;
        }

        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            return false;
        }

        if (containsDotFileSegment(file)) {
            return false;
        }

        String extension = extensionOf(file);
        if (extension.isBlank()) {
            return false;
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return false;
        }

        /*
         * Stronger symlink-escape protection.
         *
         * Even if the final file itself is not a symlink, one of its parent
         * directories could be a symlink. toRealPath() resolves that.
         */
        Path realRoot = getTemplateResourceDirectoryPath(templatePath)
                .toRealPath(LinkOption.NOFOLLOW_LINKS)
                .normalize();

        Path realFile = file.toRealPath(LinkOption.NOFOLLOW_LINKS)
                .normalize();
        if (!realFile.startsWith(realRoot)) {
            return false;
        }

        long size = Files.size(file);
        return size <= MAX_FILE_SIZE_BYTES;
    }

    private @NonNull String extensionOf(@NonNull Path file) {
        String name = file.getFileName().toString();
        int index = name.lastIndexOf('.');

        if (index < 0 || index == name.length() - 1) {
            return "";
        }

        return name.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    private boolean containsDotFileSegment(@NonNull Path path) {
        for (Path segment : path) {
            String name = segment.toString();
            if (name.startsWith(".")) {
                return true;
            }
        }

        return false;
    }

    private Mono<Void> writeResource(@NonNull HttpServerResponse response, @NonNull HttpServerRequest request,
                                     @NonNull StaticResource resource) {

        response.status(HttpResponseStatus.OK);
        response.header(HttpHeaderNames.CONTENT_TYPE, resource.contentType());
        response.header(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(resource.size()));
        response.header("X-Content-Type-Options", "nosniff");
        response.header("Cache-Control", "no-store");
        response.header("Pragma", "no-cache");
        response.header("Access-Control-Allow-Credentials", "true");
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Vary", "Origin");
        response.header("Access-Control-Allow-Private-Network", "true");

        if (request.method().name().equals("HEAD")) {
            return response.send();
        }

        return response.sendFile(resource.path()).then();
    }
}
