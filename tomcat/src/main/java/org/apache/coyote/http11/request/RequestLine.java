package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    private static final String REQUEST_API_DELIMITER = " ";
    private static final int START_LINE_SIZE = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String QUERY_STRING_SYMBOL = "?";
    private static final String DOT = ".";

    private final HttpMethod method;
    private final String uri;
    private final String version;

    private RequestLine(final HttpMethod method, final String uri, final String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine from(final BufferedReader bufferedReader) throws IOException {
        final String requestApi = bufferedReader.readLine();
        final String[] apiInfo = requestApi.split(REQUEST_API_DELIMITER);

        if (apiInfo.length != START_LINE_SIZE) {
            throw new IllegalArgumentException("잘못된 http 요청 입니다.");
        }

        return new RequestLine(HttpMethod.valueOf(apiInfo[HTTP_METHOD_INDEX]), apiInfo[REQUEST_URI_INDEX],
                apiInfo[HTTP_VERSION_INDEX]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasQueryString() {
        return uri.contains(QUERY_STRING_SYMBOL);
    }

    public String getPath() {
        if (hasQueryString()) {
            final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
            return uri.substring(0, queryIndex);
        }
        return uri;
    }

    public boolean isStaticRequest() {
        return uri.contains(DOT) || uri.equals("/");
    }

    public String getExtension() {
        final int dotIndex = uri.indexOf(DOT);
        return uri.substring(dotIndex + 1);
    }

    public boolean isGetMethod() {
        return method.isGet();
    }
}
