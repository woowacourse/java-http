package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
    }

    public HttpResponse(HttpStatus status, Map<String, String> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public boolean isError() {
        return status != null && status.isClientError();
    }

    public byte[] getBytes() {
        return getResponse().getBytes();
    }

    private String getResponse() {
        List<String> response = new ArrayList<>();
        response.add("HTTP/1.1 " + status.getCode() + " " + status.name() + " ");
        for (String key : headers.keySet()) {
            response.add(key + ": " + headers.get(key));
        }
        response.add("");
        response.add(body);
        return String.join("\r\n", response);
    }

    public int getCode() {
        return status.getCode();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
