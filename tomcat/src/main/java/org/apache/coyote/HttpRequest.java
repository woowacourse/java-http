package org.apache.coyote;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeader;
import org.apache.coyote.http.request.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String name){
        return requestLine.getParameter(name);
    }

    public String getBody(String key) {
        return body.getValue(key);
    }
}
