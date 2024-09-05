package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.HttpRequest;

public class Http11Request implements HttpRequest {

    private final Http11RequestLine requestLine;
    private final Http11RequestHeaders headers;
    private final Http11RequestBody body;

    public Http11Request(
            Http11RequestLine requestLine,
            Http11RequestHeaders headers,
            Http11RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String getMethod() {
        return requestLine.getMethod();
    }

    @Override
    public String getRequestURI() {
        return requestLine.getURI();
    }

    @Override
    public String getPath() {
        return requestLine.getPath();
    }

    @Override
    public boolean isNotExistsCookie(String key) {
        return !headers.existsCookie(key);
    }

    @Override
    public String getHeader(String header) {
        return headers.getValue(header);
    }

    @Override
    public Map<String, String> getParsedBody() {
        return body.parseBody();
    }

}
