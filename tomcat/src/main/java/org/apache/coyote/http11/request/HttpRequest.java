package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpHeader;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader headers;
    private RequestBody body;

    public HttpRequest(RequestLine requestLine, HttpHeader headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public String getPath() {
        return requestLine.path();
    }

    public String getVersion() {
        return requestLine.version();
    }

    public Map<String, String> getHeaders() {
        return headers.headers();
    }

    public String getSessionId() {
        return headers.getSessionId().orElse("");
    }

    public String getBody() {
        return body.body();
    }

    public void setBody(String body) {
        this.body = new RequestBody(body);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "body=" + body +
                ", requestLine=" + requestLine +
                ", header=" + headers +
                '}';
    }
}
