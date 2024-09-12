package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {
    private final StatusLine statusLine;
    private final HttpResponseHeader responseHeader;
    private final HttpResponseBody responseBody;

    private HttpResponse() {
        statusLine = StatusLine.defaultStatusLine();
        responseHeader = new HttpResponseHeader();
        responseBody = new HttpResponseBody();
    }

    public static HttpResponse defaultResponse() {
        return new HttpResponse();
    }

    public void addHeader(String key, String value) {
        responseHeader.put(key, value);
    }

    public void addBody(String body) {
        responseBody.setBody(body);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader.getHeaders();
    }

    public String getResponseBody() {
        return responseBody.getBody();
    }

    public void setStatusLine(String version, HttpStatusCode httpStatusCode) {
        statusLine.setVersion(version);
        statusLine.setStatusCode(httpStatusCode);
    }
}
