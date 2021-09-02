package nextstep.jwp.server.exception;

import nextstep.jwp.server.http.response.HttpStatus;

public class HttpStatusException extends RuntimeException {

    private final String path;
    private final HttpStatus httpStatus;

    public HttpStatusException(String path, HttpStatus httpStatus, String message) {
        super(message);
        this.path = path;
        this.httpStatus = httpStatus;
    }

    public String getPath() {
        return path;
    }

    public HttpStatus statusCode() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
