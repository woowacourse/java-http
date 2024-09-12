package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private StatusLine statusLine;
    private Map<String, String> responseHeaders;
    private String responseBody;

    public HttpResponse() {
        this.responseHeaders = new HashMap<>();
        this.responseBody = "";
    }

    public void setStatusLine(Status status) {
        statusLine = new StatusLine("HTTP/1.1", status);
    }

    public void setContentType(String contentType) {
        responseHeaders.put("Content-Type", contentType + ";charset=utf-8 ");
    }

    public void setContentLength(int contentLength) {
        responseHeaders.put("Content-Length", String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        responseHeaders.put("Location", location);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setCookie(String sessionId) {
        responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId);
    }

    public String getResponse() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\r\n");
        serializeStatusLine(stringBuilder);
        serializeResponseHeaders(stringBuilder);
        stringBuilder.append("").append("\r\n");
        serializeResponseBody(stringBuilder);

        return stringBuilder.toString();
    }

    private void serializeStatusLine(StringBuilder stringBuilder) {
        stringBuilder.append(statusLine.serialize()).append("\r\n");
    }

    private void serializeResponseHeaders(StringBuilder stringBuilder) {
        for (Entry<String, String> responseHeaders : responseHeaders.entrySet()) {
            stringBuilder.append(responseHeaders.getKey() + ": " + responseHeaders.getValue() + " ").append("\r\n");
        }
    }

    private void serializeResponseBody(StringBuilder stringBuilder) {
        stringBuilder.append(responseBody);
    }
}
