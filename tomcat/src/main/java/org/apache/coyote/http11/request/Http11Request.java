package org.apache.coyote.http11.request;

public class Http11Request {

    public static final String LINE_SEPARATOR = "\r\n";
    final Method method;
    final String uri;

    public Http11Request(final Method method, final String uri) {
        this.method = method;
        this.uri = uri;
    }

    public static Http11Request from(final String request) {
        final String[] lines = request.split(LINE_SEPARATOR);

        final String[] requestUri = lines[0].split(" ");
        final String method = requestUri[0];
        final String uri = requestUri[1];

        return new Http11Request(
                Method.getMethod(method),
                uri
        );
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
