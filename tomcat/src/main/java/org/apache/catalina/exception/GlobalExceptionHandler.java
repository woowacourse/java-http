package org.apache.catalina.exception;

import java.rmi.ServerException;
import javassist.NotFoundException;
import org.apache.coyote.http11.http.HttpStatus;

public class GlobalExceptionHandler {

    public ErrorPage resolve(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return new ErrorPage("static/404.html", HttpStatus.BAD_REQUEST);
        }
        if (e instanceof SecurityException) {
            return new ErrorPage("static/401.html", HttpStatus.FORBIDDEN);
        }
        if (e instanceof UnsupportedOperationException) {
            return new ErrorPage("static/404.html", HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (e instanceof ServerException) {
            return new ErrorPage("static/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (e instanceof NotFoundException) {
            return new ErrorPage("static/404.html", HttpStatus.BAD_REQUEST);
        }
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return new ErrorPage("static/404.html", HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (e instanceof HttpVersionNotSupported) {
            return new ErrorPage("static/404.html", HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
        }

        return new ErrorPage("static/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ErrorPage(String path, HttpStatus status) {}
}
