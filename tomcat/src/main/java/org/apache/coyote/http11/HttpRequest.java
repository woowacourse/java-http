package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final String startLine;
    private String body;
    private Map<String, String> headers = new LinkedHashMap<>();

    public HttpRequest(String startLine) {
        this.startLine = startLine;
    }

    public String method() {
        return startLine.split(" ")[0];
    }

    public String uri() {
        String uri = startLine.split(" ")[1];
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf('?'));
        }
        return uri;
    }

    public String quiryString() {
        String uri = startLine.split(" ")[1];
        if (uri.contains("?")) {
            return uri.substring(uri.indexOf('?') + 1);
        }
        return null;
    }

    public String httpVersion() {
        return startLine.split(" ")[2];
    }

    public String body() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }
}
