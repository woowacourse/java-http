package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

public abstract class HttpRequest {

    private final List<String> clientData;
    private final String VERSION;
    private final HttpHeaders headers;
    private final HttpPayload payload;
    private final HttpRequestLine httpRequestLine;


    public HttpRequest(List<String> clientData, String version) {
        this.clientData = clientData;
        this.VERSION = version;
        this.httpRequestLine = HttpRequestLine.from(clientData);
        this.headers = HttpHeaders.from(clientData);
        this.payload = HttpPayload.from(clientData);
    }

    public HttpMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public String getLocation() {
        return httpRequestLine.getLocation();
    }

    public String getVersion() {
        return httpRequestLine.getVersion();
    }

    public String getHeader(String key) {
        return headers.find(key);
    }
    public String getCookie() {
        return headers.find("Cookie").split("=")[1];
    }

    public Map<String, String> getQueries(){
        return httpRequestLine.getQueries();}

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
