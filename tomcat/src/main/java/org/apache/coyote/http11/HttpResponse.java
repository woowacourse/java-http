package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeaderField.*;
import static org.apache.coyote.http11.Protocol.*;

public class HttpResponse {

    private static final String KEY_VALUE_DELIMITER = "=";
    private StatusLine statusLine;
    private HttpResponseHeaders httpHeaders;
    private String responseBody;

    public HttpResponse() {
        this.httpHeaders = new HttpResponseHeaders();
        this.responseBody = "";
    }

    public void setStatusLine(Status status) {
        statusLine = new StatusLine(HTTP_1_1, status);
    }

    public void setContentType(String contentType) {
        httpHeaders.setField(CONTENT_TYPE, contentType + ";charset=utf-8 ");
    }

    public void setContentLength(int contentLength) {
        httpHeaders.setField(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        httpHeaders.setField(LOCATION, location);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setCookie(String key, String value) {
        httpHeaders.setField(SET_COOKIE, key + KEY_VALUE_DELIMITER + value);
    }

    public String getStatusMessage() {
        return this.statusLine.getStatusMessage();
    }

    public String getContentType() {
        return this.httpHeaders.findField(CONTENT_TYPE);
    }

    public int getContentLength() {
        return Integer.parseInt(this.httpHeaders.findField(CONTENT_LENGTH));
    }

    public String getResponse() {
        StringBuilder stringBuilder = new StringBuilder();

        getStatusLineToString(stringBuilder);
        getHeadersToString(stringBuilder);
        stringBuilder.append("\r\n");
        getResponseBodyToString(stringBuilder);

        return stringBuilder.toString();
    }

    private void getStatusLineToString(StringBuilder stringBuilder) {
        stringBuilder.append(statusLine.getStatusLineToString()).append("\r\n");
    }

    private void getHeadersToString(StringBuilder stringBuilder) {
        httpHeaders.getHeadersToString(stringBuilder);
    }

    private void getResponseBodyToString(StringBuilder stringBuilder) {
        stringBuilder.append(responseBody);
    }
}
