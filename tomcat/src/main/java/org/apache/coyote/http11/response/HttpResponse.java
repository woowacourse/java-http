package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.spec.HttpStatus;
import org.apache.coyote.http11.response.spec.MimeType;

public class HttpResponse {

    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CHARSET = "charset=utf-8";
    private static final MimeType DEFAULT_MIME_TYPE = MimeType.HTML;

    private HttpStatus httpStatus;
    private MimeType mimeType;
    private String body;

    public HttpResponse(HttpStatus httpStatus, MimeType mimeType, String body) {
        this.httpStatus = httpStatus;
        this.mimeType = mimeType;
        this.body = body;
    }

    public HttpResponse() {
        this(null, DEFAULT_MIME_TYPE, "");
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                processStartLine(),
                processHeaders(),
                "",
                body).getBytes();
    }

    private String processStartLine() {
        String codeName = httpStatus.name();
        int code = httpStatus.getCode();
        return String.format("%s %d %s", DEFAULT_HTTP_VERSION, code, codeName);
    }

    private String processHeaders() {
        String contentType = mimeType.getValue();
        int contentLength = body.getBytes().length;
        return String.format("Content-Type: %s;%s\r\nContent-Length: %d", contentType, DEFAULT_CHARSET,
                contentLength);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
