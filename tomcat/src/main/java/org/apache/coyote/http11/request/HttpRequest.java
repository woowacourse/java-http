package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.request.requestline.HttpRequestLine;

public class HttpRequest {
    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpPayload payload;


    public HttpRequest(List<String> clientData) {
        this.requestLine = HttpRequestLine.from(clientData);
        this.headers = HttpHeaders.from(clientData);
        this.payload = HttpPayload.from(clientData);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getLocation() {
        return requestLine.getLocation().getFileName();
    }

    public String getExtension() {
        return requestLine.getLocation().getExtension();
    }

    public String getCookieValue(String key) {
        String value = headers.getCookieValue(key);
        if (Objects.isNull(value)) {
            return null;
        }
        return value;
    }

    public boolean containsHeader(String key) {
        return headers.contains(key);
    }

    public Map<String, String> getPayload() {
        return payload.getValue();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
               "requestLine=" + requestLine +
               ", headers=" + headers +
               ", payload=" + payload +
               '}';
    }
}
