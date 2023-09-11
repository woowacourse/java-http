package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Charset;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.MediaType;

import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, String body) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public Optional<String> sessionId() {
        return httpHeaders.sessionId();
    }

    public String getContentTypeByAcceptHeader() {
        List<String> acceptHeaderValues = httpHeaders.get("Accept");
        if (acceptHeaderValues != null && acceptHeaderValues.contains(MediaType.TEXT_CSS.getValue())) {
            return ContentType.of(MediaType.TEXT_CSS, Charset.UTF_8).format();
        }
        return ContentType.of(MediaType.TEXT_HTML, Charset.UTF_8).format();
    }

    public String method() {
        return requestLine.getHttpMethod();
    }

    public String path() {
        return requestLine.getPath();
    }

    public String httpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getBody() {
        return body;
    }
}
