package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.Constants.SPACE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestURI {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;

    private RequestURI(final String httpMethod, final String uri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static RequestURI from(final String requestURILine) {
        final List<String> requestURI = Arrays.stream(requestURILine.split(SPACE))
                .collect(Collectors.toUnmodifiableList());

        return new RequestURI(
                requestURI.get(HTTP_METHOD_INDEX),
                requestURI.get(URI_INDEX),
                requestURI.get(HTTP_VERSION_INDEX)
        );
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

}
