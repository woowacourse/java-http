package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String httpMethod;
    private final String requestUri;
    private final String httpVersion;

    public RequestLine(String httpMethod, String requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        List<String> requests = Arrays.stream(requestLine.split(DELIMITER))
                .map(String::trim)
                .collect(toList());
        return new RequestLine(requests.get(HTTP_METHOD_INDEX), requests.get(REQUEST_URI_INDEX), requests.get(
                HTTP_VERSION_INDEX));
    }

    public String httpMethod() {
        return httpMethod;
    }

    public String requestUri() {
        return requestUri;
    }

    public String httpVersion() {
        return httpVersion;
    }
}
