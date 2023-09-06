package org.apache.coyote.http.request;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.HttpHeader;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader header;
    private final String body;

    public HttpRequest(HttpRequestLine requestLine, HttpHeader header, String body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Optional<String> getSessionId() {
        return header.getSessionId();
    }

    public Map<String, String> getParameters() {
        HttpParameters parameters = requestLine.getParameters();

        Optional<ContentType> optionalContentType = header.getContentType();
        if (optionalContentType.isPresent()) {
            ContentType contentType = optionalContentType.get();
            if (contentType.isFormUrlEncoded()) {
                parameters.addAll(HttpParameters.from(body));
            }
        }
        return parameters.getParameters();
    }
}
