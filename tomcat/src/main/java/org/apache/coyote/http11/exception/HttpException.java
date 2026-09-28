package org.apache.coyote.http11.exception;

public final class HttpException extends RuntimeException {

    public enum Status {
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        private final int code;
        private final String reasonPhrase;

        Status(final int code, final String reasonPhrase) {
            this.code = code;
            this.reasonPhrase = reasonPhrase;
        }

        public int code() {
            return code;
        }

        public String reasonPhrase() {
            return reasonPhrase;
        }
    }

    private final Status status;

    public HttpException(final Status status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpException(final Status status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public Status status() {
        return status;
    }
}
