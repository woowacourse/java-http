package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        String method = requestLine.getMethod();
        return HttpMethod.valueOf(method);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getContentType() {
        return header.getHeader(HttpHeaders.CONTENT_TYPE.getName()).split(",")[0];
    }

    public HttpCookie getCookie() {
        String cookieValue = header.getHeader(HttpHeaders.COOKIE.getName());
        return new HttpCookie(cookieValue);
    }

    public Map<String, String> getFormData() {
        return requestBody.getFormData();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
