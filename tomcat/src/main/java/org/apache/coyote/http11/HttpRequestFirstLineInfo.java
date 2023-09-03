package org.apache.coyote.http11;

public class HttpRequestFirstLineInfo {

    private static final String SPACE = " ";
    private static final String QUERY_STRING_DELIMITER = "?";

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final int NUMBER_OF_FIRST_LINE_INFOS = 3;

    private final HttpMethod httpMethod;
    private final String uri;
    private final String versionOfTheProtocol;

    private HttpRequestFirstLineInfo(HttpMethod httpMethod, String uri, String versionOfTheProtocol) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.versionOfTheProtocol = versionOfTheProtocol;
    }

    public static HttpRequestFirstLineInfo from(String firstLine) {
        String[] infos = firstLine.split(SPACE);
        if (infos.length < NUMBER_OF_FIRST_LINE_INFOS) {
            throw new IllegalArgumentException("유효하지 않은 요청 헤더 첫 줄 입니다.");
        }

        HttpMethod httpMethod = parseHttpMethod(infos[HTTP_METHOD_INDEX]);
        String uriWithQueryString = infos[URI_INDEX];
        String versionOfTheProtocol = infos[PROTOCOL_VERSION_INDEX];

        int indexOfQueryStringDelimiter = uriWithQueryString.indexOf(QUERY_STRING_DELIMITER);
        if (indexOfQueryStringDelimiter != -1) {
            String uri = uriWithQueryString.substring(0, indexOfQueryStringDelimiter);
            return new HttpRequestFirstLineInfo(httpMethod, uri, versionOfTheProtocol);
        }

        return new HttpRequestFirstLineInfo(httpMethod, uriWithQueryString, versionOfTheProtocol);
    }

    private static HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 HTTP 메서드입니다.");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getVersionOfTheProtocol() {
        return versionOfTheProtocol;
    }
}
