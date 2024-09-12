package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader headers;
    private final ResponseBody responseBody;

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.headers = new ResponseHeader();
        this.responseBody = new ResponseBody();
    }

    public void setStatusLine(String statusCode, String statusMessage) {
        statusLine.setStatusCode(statusCode);
        statusLine.setStatusMessage(statusMessage);
    }

    public void setFieldValue(String field, String value) {
        headers.setFieldValue(field, value);
    }

    public void setBody(String body) {
        this.responseBody.setBody(body);
    }

    public String getVersion() {
        return statusLine.getVersion();
    }

    public String getStatusCode() {
        return statusLine.getStatusCode();
    }

    public String getStatusMessage() {
        return statusLine.getStatusMessage();
    }

    public Map<String, String> getResponseHeader() {
        return headers.getResponseHeader();
    }

    public String getBody() {
        return responseBody.getBody();
    }
}
