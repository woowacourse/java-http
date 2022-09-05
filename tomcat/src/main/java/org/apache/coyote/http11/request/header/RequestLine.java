package org.apache.coyote.http11.request.header;

import java.util.Map;

import org.apache.coyote.http11.common.HttpMessageConfig;

public class RequestLine {

    private static final int METHOD = 0;
    private static final int RESOURCE = 1;
    private static final int HTTP_VERSION = 2;

    private final Method method;
    private final Resource resource;
    private final String httpVersion;

    private RequestLine(final Method method, final Resource resource, final String httpVersion) {
        this.method = method;
        this.resource = resource;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestLine) {
        final String[] requestLineElement = requestLine.split(HttpMessageConfig.WORD_DELIMITER.getValue());

        return new RequestLine(
            Method.findBy(requestLineElement[METHOD]),
            Resource.from(requestLineElement[RESOURCE]),
            requestLineElement[HTTP_VERSION]
        );
    }

    public String getUrl() {
        return resource.getUrl();
    }

    public Map<String, String> getQueries() {
        return resource.getQueries();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
