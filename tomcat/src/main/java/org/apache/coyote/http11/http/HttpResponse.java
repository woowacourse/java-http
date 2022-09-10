package org.apache.coyote.http11.http;

import java.io.BufferedWriter;
import java.io.IOException;
import org.apache.coyote.util.RequestContentTypeUtils;

public class HttpResponse {

    private final BufferedWriter outputStream;
    private final HttpVersion version = HttpVersion.HTTP_1_1;
    private HttpStatus status;
    private HttpHeaders headers;
    private String body = "";

    public HttpResponse(final BufferedWriter outputStream) {
        this.outputStream = outputStream;
        this.headers = new HttpHeaders();
    }

    public void defaultForward() {
        status = HttpStatus.OK;
        headers.addContentType(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue());
    }

    public void forward(final HttpPath path) {
        status = HttpStatus.OK;
        ContentType contentType = RequestContentTypeUtils.find(path.getValue());
        headers.addContentType(contentType.getValue());
    }

    public void errorForward(final HttpStatus notFound, final HttpPath path) {
        forward(path);
        status = notFound;
    }

    public void redirect(final String url) {
        headers.addLocation(url);
    }

    public void write() throws IOException {
        if (body != null) {
            headers.addContentLength(body.getBytes().length);
        }

        outputStream.write(version.getValue() + " " + status.getValue() + " \r\n"
                + headers.getResponse() + "\r\n"
                + body);
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
