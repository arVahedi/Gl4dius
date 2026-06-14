package io.gl4dius.cli.service.proxy;

import io.gl4dius.cli.model.dto.proxy.ProxyResponse;
import io.gl4dius.cli.utility.HttpHeaderUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyRequestExchanger {

    private final HttpClient httpClient = HttpClient.create().followRedirect(false);

    public Mono<ProxyResponse> exchange(HttpMethod method, String url, HttpHeaders headers, byte[] body) {
        byte[] finalBody = body == null ? new byte[0] : body;

        return this.httpClient
                .headers(outgoingHeaders -> {
                    headers.forEach(entry -> {
                        String name = entry.getKey();
                        String value = entry.getValue();
                        if (!HttpHeaderUtil.isHopByHopHeader(name)) {
                            outgoingHeaders.set(name, value);
                        }
                    });
                    outgoingHeaders.remove(HttpHeaderNames.HOST);
                    if (finalBody.length == 0) {
                        outgoingHeaders.remove(HttpHeaderNames.CONTENT_LENGTH);
                        outgoingHeaders.remove(HttpHeaderNames.CONTENT_TYPE);
                    }
                })
                .request(method)
                .uri(url)
                .send(Mono.just(Unpooled.wrappedBuffer(finalBody)))
                .responseSingle((originResponse, responseBody) ->
                        responseBody.asByteArray()
                                .defaultIfEmpty(new byte[0])
                                .map(bodyBytes -> {
                                    var responseHeaders = new DefaultHttpHeaders();
                                    originResponse.responseHeaders().forEach(entry -> {
                                        if (!HttpHeaderUtil.isHopByHopHeader(entry.getKey())
                                                && !HttpHeaderUtil.isHSTS(entry.getKey())) {
                                            responseHeaders.add(entry.getKey(), entry.getValue());
                                        }
                                    });

                                    return new ProxyResponse(
                                            originResponse.status(),
                                            responseHeaders,
                                            bodyBytes
                                    );
                                })
                );

    }
}
