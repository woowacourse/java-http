package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;

import java.util.Map;

public class HttpResponse {
    private final StatusLine statusLine;
    private final HttpResponseHeader responseHeader;
    private String responseBody;

    private HttpResponse() {
        statusLine = StatusLine.defaultStatusLine();
        responseHeader = new HttpResponseHeader();
        responseBody = "";
    }

    public static HttpResponse defaultResponse() {
        return new HttpResponse();
    }

    public void addHeader(String key, String value) {
        responseHeader.put(key, value);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader.getHeaders();
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String body) {
        this.responseBody = body;
    }

    public void setStatusLine(String version, HttpStatusCode httpStatusCode) {
        statusLine.setVersion(version);
        statusLine.setStatusCode(httpStatusCode);
    }

    public void redirect(String version, String path) {
        statusLine.setVersion(version);
        statusLine.setStatusCode(FOUND);
        responseHeader.put("Location", path);
    }
}
