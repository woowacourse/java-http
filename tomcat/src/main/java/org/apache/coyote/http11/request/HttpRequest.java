package org.apache.coyote.http11.request;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final String body;
    private final RequestParameters parameters;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers,
                        final String body, final RequestParameters parameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.parameters = parameters;
    }

    public static HttpRequest of(
            final RequestLine requestLine, final RequestHeaders headers, final String body) {

        final RequestParameters parameters = RequestParameters.of(
                requestLine.getQueryString(), headers.getHeader("Content-Type"), body);
        return new HttpRequest(requestLine, headers, body, parameters);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getHeader("Cookie"));
    }

    public String getParameter(final String name) {
        return parameters.getParameter(name);
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public String getBody() {
        return body;
    }
}
