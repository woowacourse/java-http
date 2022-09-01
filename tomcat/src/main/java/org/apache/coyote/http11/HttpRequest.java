package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> queryParameters;
    private final String version;
    private final Map<String, String> headers;

    private HttpRequest(final HttpMethod httpMethod, final String uri, final Map<String, String> queryParameters,
                        final String version,
                        final Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.version = version;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestMessage) {
        List<String> requestMessageLines = Arrays.asList(requestMessage.split("\r\n"));

        String requestLine = requestMessageLines.get(0);
        List<String> requestLines = Arrays.asList(requestLine.split(" "));
        String requestTarget = requestLines.get(1);
        String uri = requestTarget.split("\\?")[0];
        if (!(uri.equals("/") || uri.contains("."))) {
            uri += ".html";
        }
        Map<String, String> queryString = parseQueryString(requestTarget);
        String httpVersion = requestLines.get(2);

        Map<String, String> headers = toMap(requestMessageLines.subList(1, requestMessageLines.size()), ": ");
        return new HttpRequest(HttpMethod.valueOf(requestLines.get(0)), uri, queryString, httpVersion, headers);
    }

    private static Map<String, String> parseQueryString(final String requestTarget) {
        if (requestTarget.contains("?")) {
            String queryString = requestTarget.split("\\?")[1];
            return toMap(Arrays.asList(queryString.split("&")), "=");
        }
        return null;
    }

    private static Map<String, String> toMap(final List<String> toBeParsed, final String delimiter) {
        return toBeParsed.stream()
                .map(line -> line.split(delimiter))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public String getContentType() {
        return "text/" + uri.split("\\.")[1];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
