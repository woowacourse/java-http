package org.apache.coyote.http11.request.mapping;

import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.support.FileUtils;

public class NotFoundRequestHandler implements RequestHandler {

    private static final String NOT_FOUND_HTML = "/404.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String notFoundHtmlPath = FileUtils.getStaticFilePathFromUri(NOT_FOUND_HTML);

        final String responseBody = FileUtils.readContentByStaticFilePath(notFoundHtmlPath);
        return new HttpResponse(
                ContentType.TEXT_HTML,
                HttpStatus.NOT_FOUND,
                new HttpHeaders(Map.of()),
                responseBody
        );
    }
}
