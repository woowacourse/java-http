package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

import java.io.IOException;

public class ExceptionHandler {

    private static final String RESOURCE_PATH_FORMAT = "static/%s.html";

    public HttpResponse handleException(HttpException e) throws IOException {
        HttpStatus httpStatus = e.httpStatus();
        int code = httpStatus.statusCode();
        ResourceLoader resourceLoader = new ResourceLoader();
        String body = resourceLoader.loadResourceAsString(String.format(RESOURCE_PATH_FORMAT, code));
        return HttpResponse.from(httpStatus, TEXT_HTML, body);
    }
}
