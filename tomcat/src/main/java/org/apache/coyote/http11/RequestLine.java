package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod method;
    private final String url;
    private final String version;

    private RequestLine(HttpMethod method, String url, String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineToken = requestLine.split(" ");

        HttpMethod method = HttpMethod.from(requestLineToken[0]);
        String url = requestLineToken[1];
        String version = requestLineToken[2];

        return new RequestLine(method, url, version);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
