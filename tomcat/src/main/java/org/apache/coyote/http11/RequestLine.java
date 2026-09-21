package org.apache.coyote.http11;

public class RequestLine {
    private final HttpMethod method;
    private final Uri uri;
    private final String version;

    private RequestLine(HttpMethod method, Uri uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine from (String rawRequestLine) {
        final String[] requestLineArray = rawRequestLine.split(" ");
        final String httpMethod = requestLineArray[0];
        final String url = requestLineArray[1];
        final String httpVersion = requestLineArray[2];
        final HttpMethod method = HttpMethod.from(httpMethod);
        final Uri uri = Uri.create(url);
        return new RequestLine(method, uri, httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Uri getUri() {
        return uri;
    }
}
