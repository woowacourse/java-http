package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestStartLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final Url url;

    private RequestStartLine(final HttpMethod httpMethod, final Url url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public static RequestStartLine from(final String request) throws URISyntaxException {
        String[] requestSegment = request.split(START_LINE_DELIMITER);
        return new RequestStartLine(
                HttpMethod.from(requestSegment[HTTP_METHOD_INDEX]),
                Url.parseUrl(new URI(requestSegment[HTTP_PATH_INDEX]))
        );
    }

    public boolean isGet() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public Url getPath() {
        return url;
    }
}
