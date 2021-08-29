package nextstep.jwp.core.exception;

import nextstep.jwp.webserver.response.StatusCode;

public class StatusException extends RuntimeException {

    private final StatusCode statusCode;
    private final String page;

    public StatusException(StatusCode statusCode, String page) {
        this.statusCode = statusCode;
        this.page = page;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getPage() {
        return page;
    }
}
