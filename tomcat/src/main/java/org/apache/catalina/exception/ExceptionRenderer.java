package org.apache.catalina.exception;

import java.io.IOException;
import org.apache.catalina.controller.util.StaticResourceReader;
import org.apache.catalina.exception.GlobalExceptionHandler.ErrorPage;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.response.HttpResponse;

public class ExceptionRenderer {

    public void render(HttpResponse response, ErrorPage errorPage) {
        try {
            byte[] bytes = StaticResourceReader.readResource(errorPage.path());
            String contentType = StaticResourceReader.resolveContentType(errorPage.path());

            response.status(errorPage.status())
                    .contentType(contentType)
                    .write(bytes);
        } catch (IOException e) {
            fallback(response);
        }
    }

    private void fallback(HttpResponse response) {
        try {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(ContentType.PLAIN.value())
                    .write("500 Internal Server Error");
        } catch (IOException ignored) {}
    }
}
