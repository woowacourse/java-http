package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBuilder {

    private static final String DEFAULT_PATH = "/index";
    private final HttpMethod httpMethod;
    private final Map<String, String> queryParams;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> bodyParams;
    private final HttpCookie httpCookie;
    private String path;

    private HttpRequestBuilder(HttpMethod httpMethod, String uri, HttpStatusCode httpStatusCode) {
        this.httpMethod = httpMethod;
        queryParams = new HashMap<>();
        path = uri;
        formatPath();
        this.httpStatusCode = httpStatusCode;
        httpRequestHeaders = new HashMap<>();
        bodyParams = new HashMap<>();
        httpCookie = new HttpCookie();
    }

    public static HttpRequestBuilder of(HttpMethod httpMethod, String uri, HttpStatusCode httpStatusCode) {
        return new HttpRequestBuilder(httpMethod, uri, httpStatusCode);
    }

    public HttpRequestBuilder setRequestHeaders(Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders.putAll(httpRequestHeaders);
        return this;
    }

    public HttpRequestBuilder setHttpCookie(Map<String, String> cookies) {
        this.httpCookie.putAll(cookies);
        return this;
    }

    public HttpRequestBuilder setBodyParams(Map<String, String> bodyParams) {
        this.bodyParams.putAll(bodyParams);
        return this;
    }

    public HttpRequest toHttpRequest() {
        return new HttpRequest(httpMethod, path, queryParams, httpStatusCode, httpRequestHeaders, bodyParams,
            httpCookie);
    }

    private void formatPath() {
        if (path.contains("?")) {
            separateQueryParams(path);
        }

        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }

        if (!path.contains(".")) {
            path = path + ".html";
        }
    }

    private void separateQueryParams(String uri) {
        int index = uri.indexOf("?");
        path = uri.substring(0, index);

        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] queryEntry = query.split("=");
            queryParams.put(queryEntry[0], queryEntry[1]);
        }
    }

}
