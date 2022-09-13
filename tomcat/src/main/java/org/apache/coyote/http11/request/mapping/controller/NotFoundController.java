package org.apache.coyote.http11.request.mapping.controller;

import java.util.ArrayList;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.support.FileUtils;

public class NotFoundController implements Controller {

    private static final String NOT_FOUND_HTML = "/404.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String notFoundHtmlPath = FileUtils.getStaticFilePathFromUri(NOT_FOUND_HTML);

        final String responseBody = FileUtils.readContentByStaticFilePath(notFoundHtmlPath);
        return new HttpResponse(
                ContentType.TEXT_HTML,
                HttpStatus.NOT_FOUND,
                new HttpHeaders(new ArrayList<>()),
                responseBody
        );
    }
}
