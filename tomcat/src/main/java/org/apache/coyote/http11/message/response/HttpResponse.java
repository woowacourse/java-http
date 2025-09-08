package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {
    private HttpStatus status = HttpStatus.OK;
    private HttpHeaders headers = HttpHeaders.init();
    private HttpBody body = HttpBody.init();

    public void init() {
        status = HttpStatus.OK;
        headers = HttpHeaders.init();
        body = HttpBody.init();
    }

    public void appendToBody(byte[] additionalContent) {
        body = body.append(additionalContent);
    }

    public void appendToBody(String additionalText) {
        body = body.append(additionalText);
    }

    public void setContentType(ContentType contentType) {
        headers.add("Content-Type", contentType.getMimeType());
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addToHeader(String name, String value) {
        headers.add(name, value);
    }

    public void writeTo(OutputStream output) throws IOException {
        headers.add("Content-Length", String.valueOf(body.length()));
        output.write(getHeaderText().getBytes(StandardCharsets.ISO_8859_1));
        output.write(getBodyBytes());
    }

    private String getHeaderText() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append("\r\n");

        headers.getLines().forEach(line -> sb.append(line).append("\r\n"));
        sb.append("\r\n");
        return sb.toString();
    }

    private byte[] getBodyBytes() {
        return body.getBytes();
    }
}
