package org.apache.coyote.http11;

public class HttpRequestStartLine {

    private final String httpMethod;
    private final String rawUri;
    private final String httpVersion;

    public HttpRequestStartLine(final String httpMethod, final String uri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.rawUri = uri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine from(final String line) {
        return parseStartLine(line);
    }

    private static HttpRequestStartLine parseStartLine(final String line) {
        String[] tokens = line.trim().split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("invalid request start line: " + line);
        }
        String httpMethod = tokens[0].trim();
        String path = tokens[1].trim();
        String httpVersion = tokens[2].trim();

        return new HttpRequestStartLine(httpMethod, path, httpVersion);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        int queryIndex = rawUri.indexOf("?");
        if (queryIndex >= 0) {
            return rawUri.substring(0, queryIndex);
        }
        return rawUri;
    }

    public String getQueryString() {
        int queryIndex = rawUri.indexOf("?");
        if (queryIndex >= 0 && queryIndex + 1 < rawUri.length()) {
            return rawUri.substring(queryIndex + 1);
        }
        return "";
    }
}
