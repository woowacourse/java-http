package nextstep.jwp.http.request;


import nextstep.jwp.http.session.HttpSession;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean hasMethod(HttpMethod method) {
        return requestLine.hasMethod(method);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }

    public RequestCookie getCookie() {
        return headers.getHttpCookie();
    }

    public HttpSession getSession() {
        return headers.getSession();
    }
}
