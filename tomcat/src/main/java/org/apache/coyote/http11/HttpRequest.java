package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestMethod requestMethod;
    private final String requestUrl;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

    public HttpRequest(
            final RequestMethod requestMethod,
            final String requestUrl,
            final String httpVersion,
            final Map<String, String> headers,
            final Map<String, String> parameters
    ) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.parameters = parameters;
    }

    public static HttpRequest from(final String rawHttpRequest) {
        String[] parts = rawHttpRequest.split("\r\n\r\n", 2);

        String headerPart = parts[0];
        String bodyPart = parts.length > 1 ? parts[1] : "";

        String[] headerLines = headerPart.split("\r\n");
        String[] requestLine = headerLines[0].trim().split(" ");
        RequestMethod requestMethod = RequestMethod.valueOf(requestLine[0]);
        String requestUrl = requestLine[1];
        Map<String, String> parameters = new HashMap<>();
        if (requestUrl.contains("?")) {
            int index = requestUrl.indexOf("?");
            String queryStrings = requestUrl.substring(index + 1);
            putParameters(queryStrings, parameters);
            requestUrl = requestUrl.substring(0, index);
        }
        String requestHttpVersion = requestLine[2];

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            String[] kv = headerLines[i].split(":", 2);
            String name = kv[0].trim();
            String value = kv[1].trim();
            headers.put(name, value);
        }

        if (headers.get("Content-Type") != null && headers.get("Content-Type")
                .equals("application/x-www-form-urlencoded")) {
            putParameters(bodyPart, parameters);
        }

        return new HttpRequest(requestMethod, requestUrl, requestHttpVersion, headers, parameters);
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    private static void putParameters(String queryStrings, Map<String, String> parameters) {
        for (String queryString : queryStrings.split("&")) {
            String[] strings = queryString.split("=");
            String key = strings[0];
            String value = strings[1];
            parameters.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestMethod=" + requestMethod +
                ", requestUrl='" + requestUrl + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                '}';
    }
}
