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

    public void forward(final HttpPath path) {
        if (path.getValue().equals("/")) {
            headers.addContentType(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue());
            return;
        }
        ContentType contentType = RequestContentTypeUtils.find(path.getValue());
        headers.addContentType(contentType.getValue());
    }

    public void redirect(final String url) {
        headers.addLocation(url);
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

    public void flush() throws IOException {
        if (body != null) {
            headers.addContentLength(body.getBytes().length);
        }

        outputStream.write(version.getValue() + " " + status.getValue() + " \r\n"
                + headers.getAllToString() + " " + "\r\n"
                + body);
    }
}
