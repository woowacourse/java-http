package org.apache.exception;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.response.ResponseEntity;
import org.apache.coyote.response.ResponseStatus;

public class PageRedirectException extends RuntimeException {

    private final transient ResponseEntity response;

    public PageRedirectException(final ResponseEntity response) {
        this.response = response;
    }

    public ResponseEntity getResponseEntity() {
        return response;
    }

    public static class Unauthorized extends PageRedirectException {
        public Unauthorized(HttpVersion httpVersion) {
            super(ResponseEntity.fromViewPath(httpVersion, "/401", ResponseStatus.UNAUTHORIZED));
        }
    }

    public static class PageNotFound extends PageRedirectException {
        public PageNotFound(HttpVersion httpVersion) {
            super(ResponseEntity.fromViewPath(httpVersion, "/404", ResponseStatus.NOT_FOUND));
        }
    }

    public static class ServerError extends PageRedirectException {
        public ServerError(HttpVersion httpVersion) {
            super(ResponseEntity.fromViewPath(httpVersion, "/500", ResponseStatus.INTERNET_SERVER));
        }
    }
}
