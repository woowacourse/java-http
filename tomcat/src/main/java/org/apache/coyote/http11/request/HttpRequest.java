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
        String contentType = header.getHeader(HttpHeaders.ACCEPT.getName());
        if (contentType == null) {
            return null;
        }

        return contentType.split(",")[0];
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

    public Map<String, String> getHeader() {
        return header.getHeaders();
    }
}
