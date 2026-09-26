package org.apache.coyote.http11.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.coyote.http11.HttpHeaders;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final RequestParams params;
    private final HttpHeaders headers;
    private final byte[] body;
    private final HttpCookie cookie;

    public HttpRequest(final RequestLine requestLine, final RequestParams params,
                       final HttpHeaders headers, final byte[] body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body.clone();
        this.cookie = new HttpCookie(headers.get("cookie"));
        if ("POST".equals(requestLine.method()) && headers.isFormUrlEncoded()) {
            this.params = params.append(new RequestParams(new String(this.body, UTF_8)));
        } else {
            this.params = params;
        }
    }

    public RequestParams params() {
        return params;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public byte[] body() {
        return body.clone();
    }

    public String method() {
        return requestLine.method();
    }

    public HttpCookie cookie() {
        return cookie;
    }

    public String path() {
        return requestLine.path();
    }
}
