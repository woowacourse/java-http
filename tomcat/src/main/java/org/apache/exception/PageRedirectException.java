package org.apache.exception;

import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseStatus;

public class PageRedirectException extends RuntimeException {

    private final transient HttpResponse response;
    private final String viewPath;
    private final ResponseStatus responseStatus;

    public PageRedirectException(final HttpResponse response, final String viewPath, final ResponseStatus responseStatus) {
        this.response = response;
        this.viewPath = viewPath;
        this.responseStatus = responseStatus;
    }

    public void setResponse() {
        response.setViewPathAsBodyAndSetStatus(viewPath, responseStatus);
    }

    public static class Unauthorized extends PageRedirectException {
        public Unauthorized(final HttpResponse httpResponse) {
            super(httpResponse, "/401", ResponseStatus.UNAUTHORIZED);
        }
    }

    public static class PageNotFound extends PageRedirectException {
        public PageNotFound(final HttpResponse httpResponse) {
            super(httpResponse, "/404", ResponseStatus.NOT_FOUND);
        }
    }

    public static class ServerError extends PageRedirectException {
        public ServerError(final HttpResponse httpResponse) {
            super(httpResponse, "/500", ResponseStatus.INTERNET_SERVER);
        }
    }
}
