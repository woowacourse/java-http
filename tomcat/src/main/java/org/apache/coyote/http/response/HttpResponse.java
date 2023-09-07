package org.apache.coyote.http.response;

import static org.apache.coyote.http.HeaderKey.CONTENT_LENGTH;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpHeaderConverter;
import org.apache.coyote.http.MediaType;
import org.apache.coyote.http.request.ContentType;

public class HttpResponse {

    private StatusCode statusCode;
    private final HttpHeader header;
    private String body;
    private String forwardPath;
    private boolean isCompleted;

    public HttpResponse() {
        this.header = new HttpHeader();
        this.isCompleted = false;
    }

    public byte[] getBytes() {
        String response = statusCode.renderStatusLine()
                + HttpHeaderConverter.encode(header)
                + body;

        return response.getBytes(StandardCharsets.UTF_8);
    }

    public void forward(String forwardPath) {
        this.forwardPath = forwardPath;
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void addHeader(String key, String value) {
        header.addValue(key, value);
    }

    public boolean contains(String key) {
        return header.getValue(key).isPresent();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.isCompleted = true;
    }

    public void setContentType(MediaType mediaType, Charset charset) {
        header.setContentType(mediaType, charset);
    }

    public void setBody(String body) {
        this.body = body;
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        addHeader(CONTENT_LENGTH.value, String.valueOf(bytes.length));
    }

    public HttpHeader getHeader() {
        return header;
    }

    public Optional<ContentType> getContentType() {
        return header.getContentType();
    }

    public Optional<String> getForwardPath() {
        return Optional.ofNullable(forwardPath);
    }
}
