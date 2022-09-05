package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HeaderKeys;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.common.HttpMessageDelimiter;
import org.apache.coyote.http11.response.header.StatusCode;
import org.apache.coyote.http11.response.header.StatusLine;
import org.apache.coyote.http11.util.ResourceSearcher;

public class HttpResponse {

    private static final ResourceSearcher RESOURCE_SEARCHER = new ResourceSearcher();
    private static final String FILE_REGEX = "\\.";
    private static final int EXTENSION_LOCATION = 1;

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final String body;

    private HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final String body) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpResponse of(final String httpVersion, final String resource, final String statusCode) {
        final StatusLine statusLine = new StatusLine(httpVersion, StatusCode.findBy(statusCode));

        final String contentType = selectContentType(resource);
        final String body = loadResourceContent(resource);
        final HttpHeaders httpHeaders = createHttpHeaders(contentType, body);

        return new HttpResponse(statusLine, httpHeaders, body);
    }

    private static String selectContentType(final String resource) {
        final String[] fileElements = resource.split(FILE_REGEX);

        return ContentType.getContentType(fileElements[EXTENSION_LOCATION]);
    }

    private static String loadResourceContent(final String resource) {
        return RESOURCE_SEARCHER.loadContent(resource);
    }

    private static HttpHeaders createHttpHeaders(final String contentType, final String body) {
        int length = body.getBytes().length;
        return HttpHeaders.init()
            .add(HeaderKeys.CONTENT_TYPE.getName(), contentType + ";charset=utf-8")
            .add(HeaderKeys.CONTENT_LENGTH.getName(), String.valueOf(length));
    }

    public String toMessage() {
        return String.join(HttpMessageDelimiter.LINE.getValue(),
            statusLine.toMessage(),
            httpHeaders.toMessage(),
            HttpMessageDelimiter.HEADER_BODY.getValue(),
            body
        );
    }
}
