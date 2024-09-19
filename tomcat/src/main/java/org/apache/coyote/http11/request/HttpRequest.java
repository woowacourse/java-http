package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
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

    public String getCookie() {
        return headers.find("Cookie").split("=")[1];
    }

    public boolean containsHeader(String key) {
        return headers.contains(key);
    }

    public Map<String, String> getPayload() {
        return payload.getValue();
    }
}
