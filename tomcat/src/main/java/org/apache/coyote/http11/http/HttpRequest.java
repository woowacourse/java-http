package org.apache.coyote.http11.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final Map<String, String> queryParams;
    private final HttpHeaders headers;
    private final RequestBody requestBody;
    private final HttpCookie httpCookie;

    public HttpRequest(HttpMethod httpMethod, String requestUri) {
        this(httpMethod, requestUri, Map.of(), new HttpHeaders(), new RequestBody(), new HttpCookie());
    }

    public HttpRequest(HttpMethod httpMethod, String requestUri, HttpHeaders headers, RequestBody requestBody,
                       HttpCookie httpCookie) {
        this(httpMethod, requestUri, Map.of(), headers, requestBody, httpCookie);
    }

    public HttpRequest(HttpMethod httpMethod, String requestUri, Map<String, String> queryParams, HttpHeaders headers,
                       RequestBody requestBody, HttpCookie httpCookie) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.queryParams = queryParams;
        this.headers = headers;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest of(String startLine, HttpHeaders headers, RequestBody requestBody, HttpCookie httpCookie)
            throws IOException {
        String[] splitLine = startLine.split(" ");
        HttpMethod httpMethod = HttpMethod.valueOf(splitLine[0]);

        if (!containsQueryString(startLine)) {
            return new HttpRequest(httpMethod, splitLine[1], headers, requestBody, httpCookie);
        }

        int queryDelimiterIndex = splitLine[1].indexOf("?");
        String requestUri = splitLine[1].substring(0, queryDelimiterIndex);
        String queryString = splitLine[1].substring(queryDelimiterIndex + 1);
        return new HttpRequest(httpMethod, requestUri, toQueryMap(queryString), headers, requestBody, httpCookie);
    }

    private static boolean containsQueryString(String startLine) {
        return startLine.contains("?");
    }

    private static Map<String, String> toQueryMap(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public boolean isRoot() {
        return requestUri.equals("/");
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        SessionManager.add(session);
        return session;
    }

    public String getJSessionId() {
        return httpCookie.getJSessionId();
    }
}
