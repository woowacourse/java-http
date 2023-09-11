package org.apache.coyote.http11.request;

import common.http.HttpMethod;

public class HttpRequestLine {

    private static final String SPACE = " ";
    private static final String QUERY_STRING_DELIMITER = "?";

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final int NUMBER_OF_FIRST_LINE_INFOS = 3;

    private final HttpMethod httpMethod;
    private final String path;
    private final String versionOfTheProtocol;

    private HttpRequestLine(HttpMethod httpMethod, String path, String versionOfTheProtocol) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.versionOfTheProtocol = versionOfTheProtocol;
    }

    static HttpRequestLine from(String requestLine) {
        String[] infos = requestLine.split(SPACE);
        if (infos.length != NUMBER_OF_FIRST_LINE_INFOS) {
            throw new IllegalArgumentException("유효하지 않은 Request-line 입니다.");
        }

        HttpMethod httpMethod = HttpMethod.parseHttpMethod(infos[HTTP_METHOD_INDEX]);
        String uriWithQueryString = infos[PATH_INDEX];
        String versionOfTheProtocol = infos[PROTOCOL_VERSION_INDEX];

        int indexOfQueryStringDelimiter = uriWithQueryString.indexOf(QUERY_STRING_DELIMITER);
        if (indexOfQueryStringDelimiter != -1) {
            String uri = uriWithQueryString.substring(0, indexOfQueryStringDelimiter);
            return new HttpRequestLine(httpMethod, uri, versionOfTheProtocol);
        }

        return new HttpRequestLine(httpMethod, uriWithQueryString, versionOfTheProtocol);
    }

    String getVersionOfTheProtocol() {
        return versionOfTheProtocol;
    }

    HttpMethod getHttpMethod() {
        return httpMethod;
    }

    String getPath() {
        return path;
    }
}
