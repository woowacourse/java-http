package org.apache.coyote.http11.parser;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponse {

    private static final String CONTENT_TYPE_HEADER = "ContentType : ";
    private static final String LOCATION_PREFIX = "Location: http://localhost:8080";

    private byte[] parsedContent = "".getBytes();
    private String httpResponseStatus = "";
    private String contentType = "";
    private int contentLength = 0;
    private String location = "";

    public HttpResponse() {

    }

    public byte[] getParseContent() {
        return parsedContent;
    }

    public String getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public void setContent(byte[] content) {
        if (content == null) {
            this.contentLength = 0;
            return;
        }
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
        String headers = Stream.of(
                        httpResponseStatus,
                        contentType,
                        getContentLength(),
                        location
                )
                .filter(header -> header != null && !header.isBlank())
                .collect(Collectors.joining("\r\n"));

        String body = new String(parsedContent);

        if (body.isEmpty()) {
            return headers;
        }

        return headers + "\r\n\r\n" + body;
    }

    private String getContentLength() {
        return "Content-Length: " + contentLength;
    }

    public void setLocation(String location) {
        this.location = LOCATION_PREFIX + location;
    }
}
