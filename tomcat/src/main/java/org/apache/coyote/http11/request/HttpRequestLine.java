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
        String[] infos = parseInfos(requestLine);

        HttpMethod httpMethod = HttpMethod.parseHttpMethod(infos[HTTP_METHOD_INDEX]);
        String uri = removeQueryString(infos[PATH_INDEX]);
        String versionOfTheProtocol = infos[PROTOCOL_VERSION_INDEX];

        return new HttpRequestLine(httpMethod, uri, versionOfTheProtocol);
    }

    private static String removeQueryString(String uri) {
        int indexOfQueryStringDelimiter = uri.indexOf(QUERY_STRING_DELIMITER);
        if (indexOfQueryStringDelimiter != -1) {
            return uri.substring(0, indexOfQueryStringDelimiter);
        }
        return uri;
    }

    private static String[] parseInfos(String requestLine) {
        String[] infos = requestLine.split(SPACE);
        if (infos.length != NUMBER_OF_FIRST_LINE_INFOS) {
            throw new IllegalArgumentException("유효하지 않은 Request-line 입니다.");
        }
        return infos;
    }

    HttpMethod getHttpMethod() {
        return httpMethod;
    }

    String getPath() {
        return path;
    }

    String getVersionOfTheProtocol() {
        return versionOfTheProtocol;
    }
}
