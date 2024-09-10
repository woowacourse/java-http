package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final Path path;

    private RequestLine(HttpMethod httpMethod, Path path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static RequestLine from(final String line) {
        final var texts = line.split(" ");
        final var method = HttpMethod.fromName(texts[0]);
        final var path = new Path(texts[1]);
        return new RequestLine(method, path);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "httpMethod=" + httpMethod +
               ", path=" + path.getValue() +
               '}';
    }
}
