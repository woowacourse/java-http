package org.apache.coyote.http11;

public class HttpResponse {

    private StatusLine statusLine;
    private HttpHeaders httpHeaders;
    private String responseBody;

    public HttpResponse() {
        this.httpHeaders = new HttpHeaders();
        this.responseBody = "";
    }

    public void setStatusLine(Status status) {
        statusLine = new StatusLine("HTTP/1.1", status);
    }

    public void setContentType(String contentType) {
        httpHeaders.setField("Content-Type", contentType + ";charset=utf-8 ");
    }

    public void setContentLength(int contentLength) {
        httpHeaders.setField("Content-Length", String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        httpHeaders.setField("Location", location);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setCookie(String key, String value) {
        httpHeaders.setField("Set-Cookie", key + "=" + value);
    }

    public String getStatusMessage() {
        return this.statusLine.getStatusMessage();
    }

    public String getContentType() {
        return this.httpHeaders.findField("Content-Type");
    }

    public int getContentLength() {
        return Integer.parseInt(this.httpHeaders.findField("Content-Length"));
    }

    public String getResponse() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\r\n");
        getStatusLineToString(stringBuilder);
        getHeadersToString(stringBuilder);
        stringBuilder.append("").append("\r\n");
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
