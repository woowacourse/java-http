package org.apache.coyote.http11.parser;

public class HttpResponse {

    private static final String CONTENT_TYPE_HEADER = "ContentType : ";

    private byte[] parsedContent;
    private String httpResponseStatus;
    private String contentType;
    private int contentLength;

    public HttpResponse() {

    }

    public byte[] getParseContent() {
        return parsedContent;
    }

    public String getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public void setContent(byte[] content) {
        this.contentLength = content.length;
        this.parsedContent = content;
    }

    public void setContentType(String contentType) {
        this.contentType = CONTENT_TYPE_HEADER + contentType + " ";
    }

    public void setStatusLine(String statusLine) {
        this.httpResponseStatus = statusLine;
    }

    public String getResult() {
        return String.join("\r\n", httpResponseStatus, contentType, getContentLength(), "", new String(parsedContent));
    }

    private String getContentLength() {
        return "Content-Length: " + contentLength;
    }
}
