package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.util.HttpMessageSupporter;
import org.apache.coyote.http11.util.QueryStringResolver;

public class Request {
    private static final String QUERY_PARAM_PREFIX = "?";
    private static final int NEXT_INDEX = 1;
    private static final int FIRST_INDEX = 0;
    private static final String BLANK = " ";
    private static final String EMPTY = "";

    private final String requestURI;
    private final String httpMethod;
    private Map<String, String> queryParams;
    private String requestBody;

    private Request(final String rowRequest) {
        this.requestURI = HttpMessageSupporter.getRequestURI(rowRequest);
        this.httpMethod = parseHttpMethod(rowRequest);
        if (httpMethod.equalsIgnoreCase("get")) {
            this.queryParams = parseURI(requestURI);
            return;
        }
        if (httpMethod.equalsIgnoreCase("post")) {
            this.queryParams = parseFormRequest(rowRequest);
            return;
        }
        this.requestBody = parseRequestBody(rowRequest);
    }

    private String parseRequestBody(final String rowRequest) {
        return rowRequest.split(System.lineSeparator() + System.lineSeparator())[1]
                .replaceAll(System.lineSeparator(), EMPTY);
    }

    private Map<String, String> parseFormRequest(final String rowRequest) {
        final String queryString = rowRequest.split(System.lineSeparator() + System.lineSeparator())[1]
                .replaceAll(System.lineSeparator(), EMPTY);
        return QueryStringResolver.resolve(queryString);
    }

    private String parseHttpMethod(final String rowRequest) {
        return rowRequest.split(BLANK)[FIRST_INDEX];
    }

    private Map<String, String> parseURI(final String requestURI) {
        if (!requestURI.contains(QUERY_PARAM_PREFIX)) {
            return Map.of();
        }
        final var queryString = parseQueryString(requestURI);
        return QueryStringResolver.resolve(queryString);
    }

    private static String parseQueryString(final String requestURI) {
        int index = requestURI.indexOf(QUERY_PARAM_PREFIX);
        return requestURI.substring(index + NEXT_INDEX);
    }

    public static Request from(final String rowRequest) {
        return new Request(rowRequest);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
