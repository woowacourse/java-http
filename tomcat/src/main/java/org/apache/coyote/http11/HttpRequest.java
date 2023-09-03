package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String REQUEST_API_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String DOT = ".";
    private static final String QUERY_STRING_SYMBOL = "?";

    private final String method;
    private final String uri;
    private final String version;

    public HttpRequest(final String method, final String uri, final String version) {
        validateMethod(method);
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private void validateMethod(final String method) {
        if (method.equalsIgnoreCase(POST) ||
                method.equalsIgnoreCase(GET) ||
                method.equalsIgnoreCase(PUT) ||
                method.equalsIgnoreCase(DELETE)) {
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP METHOD 입니다.");
    }

    public static HttpRequest of(String requestInfo) {
        final String[] httpRequest = requestInfo.split(REQUEST_API_DELIMITER);
        if (httpRequest.length != 3) {
            throw new IllegalArgumentException("잘못된 http 요청 입니다.");
        }
        return new HttpRequest(httpRequest[HTTP_METHOD_INDEX], httpRequest[REQUEST_URI_INDEX],
                httpRequest[HTTP_VERSION_INDEX]);
    }

    public static HttpRequest toIndex() {
        return new HttpRequest(GET, "/index.html", "HTTP/1.1");
    }

    public static HttpRequest toNotFound() {
        return new HttpRequest(GET, "/404.html", "HTTP/1.1");
    }

    public String getUri() {
        if (hasQueryString()) {
            final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
            return uri.substring(0, queryIndex);
        }
        return uri;
    }

    public boolean hasQueryString() {
        return uri.contains(QUERY_STRING_SYMBOL);
    }

    public boolean isGet() {
        return method.equalsIgnoreCase(GET);
    }

    public boolean isStaticRequest() {
        return uri.contains(DOT);
    }

    public String getExtension() {
        final int dotIndex = uri.indexOf(DOT);
        return uri.substring(dotIndex + 1);
    }

    public Map<String, String> getQueryString() {
        if (!hasQueryString()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
        final String[] queryParameters = uri.substring(queryIndex + 1).split("&");
        for (String queryParameter : queryParameters) {
            final String[] queryKeyAndValue = queryParameter.split("=");
            if (queryKeyAndValue.length != 2) {
                throw new IllegalArgumentException("잘못된 Query String 입니다.");
            }
            final String key = queryKeyAndValue[0];
            final String value = queryKeyAndValue[1];
            result.put(key, value);
        }
        return result;
    }
}
