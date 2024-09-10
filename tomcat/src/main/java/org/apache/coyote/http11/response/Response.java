package org.apache.coyote.http11.response;

import org.apache.coyote.http11.exception.NotCompleteResponseException;
import org.apache.coyote.http11.request.HttpHeaders;

public class Response {

    private final HttpHeaders headers;
    private StatusLine statusLine;
    private String content;

    public Response() {
        headers = new HttpHeaders();
    }

    public void setStatus(String protocolVersion, int statusCode, String statusText) {
        statusLine = new StatusLine(protocolVersion, statusCode, statusText);
    }

    public void addHeaders(String key, String value) {
        headers.addHeader(key, value);
    }

    public void addContent(String content) {
        headers.addHeader("Content-Length", Integer.toString(content.getBytes().length));
        this.content = content;
    }

    public String toHttpMessage() {
        if (statusLine == null) {
            throw new NotCompleteResponseException("응답이 완성되지 않았습니다.");
        }
        System.out.println("statusLine.toHttpMessage() = " + statusLine.toHttpMessage());
        System.out.println("headers.toHttpMessage() = " + headers.toHttpMessage());
        return String.join("\r\n",
                statusLine.toHttpMessage(),
                headers.toHttpMessage(),
                "",
                content);
    }
}
