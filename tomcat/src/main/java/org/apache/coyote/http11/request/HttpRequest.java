package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

public abstract class HttpRequest {
    private final HttpHeaders headers;
    private final HttpPayload payload;
    private final HttpRequestLine requestLine;


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

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String key) {
        return headers.find(key);
    }

    public String getCookie() {
        return headers.find("Cookie").split("=")[1];
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueries();
    }

    public boolean containsHeader(String key) {
        return headers.contains(key);
    }

    public void setHeader(String key, String value) {
        headers.set(key, value);
    }


    public Map<String, String> getPayload() {
        return payload.getValue();
    }
}
