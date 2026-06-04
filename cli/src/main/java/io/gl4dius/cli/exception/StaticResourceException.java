package io.gl4dius.cli.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

@Getter
public class StaticResourceException extends RuntimeException {

    private final transient HttpResponseStatus status;

    public StaticResourceException(HttpResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}
