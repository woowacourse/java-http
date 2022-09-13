package nextstep.jwp.exception;

import org.apache.coyote.http.HttpStatus;

public class CustomException extends RuntimeException {

    private HttpStatus code;
    private String message;

    public CustomException(final HttpStatus code, final String message) {
        this.code = code;
        this.message = message;
    }
}
