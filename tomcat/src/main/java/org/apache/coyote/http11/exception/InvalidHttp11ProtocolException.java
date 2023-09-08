package org.apache.coyote.http11.exception;

public class InvalidHttp11ProtocolException extends RuntimeException {

    public InvalidHttp11ProtocolException(final String httpVersion) {
        super("HTTP/1.1 버전만 지원합니다. version : " + httpVersion);
    }
}
