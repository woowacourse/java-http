package org.apache.coyote.http11;

public class HttpRequestFirstLineInfo {

    public static final String SPACE = " ";
    public static final String QUERY_STRING_DELIMITER = "?";

    public static final int HTTP_METHOD_INDEX = 0;
    public static final int URI_INDEX = 1;
    public static final int PROTOCOL_VERSION_INDEX = 2;
    public static final int NUMBER_OF_FIRST_LINE_INFOS = 3;

    private final HttpMethod httpMethod;
    private final String uri;
    private final String versionOfTheProtocol;
    private final QueryStringParser queryStringParser;

    private HttpRequestFirstLineInfo(HttpMethod httpMethod, String uri, String versionOfTheProtocol, QueryStringParser queryStringParser) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.versionOfTheProtocol = versionOfTheProtocol;
        this.queryStringParser = queryStringParser;
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
            QueryStringParser queryStringParser = QueryStringParser.from(uriWithQueryString.substring(indexOfQueryStringDelimiter + 1));
            return new HttpRequestFirstLineInfo(httpMethod, uri, versionOfTheProtocol, queryStringParser);
        }

        return new HttpRequestFirstLineInfo(httpMethod, uriWithQueryString, versionOfTheProtocol, null);
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

    public QueryStringParser getQueryStringParser() {
        return queryStringParser;
    }
}
