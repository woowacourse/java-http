package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

import nextstep.jwp.common.ResourceLoader;

public class HttpExceptionHandler {

    private static final String RESOURCE_PATH_FORMAT = "static/%s.html";

    public HttpResponse handleException(HttpException e) {
        HttpStatus httpStatus = e.httpStatus();
        int code = httpStatus.statusCode();
        ResourceLoader resourceLoader = new ResourceLoader();
        String body = resourceLoader.load(String.format(RESOURCE_PATH_FORMAT, code));
        return HttpResponse.status(httpStatus)
                .contentType(TEXT_HTML)
                .body(body)
                .build();
    }
}
