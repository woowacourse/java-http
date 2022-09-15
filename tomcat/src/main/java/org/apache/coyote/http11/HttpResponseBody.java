package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.PLAIN;
import static util.FileLoader.readFile;

import java.io.File;
import java.io.IOException;

public class HttpResponseBody {

    private final ContentType contentType;
    private final String body;

    private HttpResponseBody(final ContentType contentType, final String body) {
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponseBody of(final String body) {
        return new HttpResponseBody(PLAIN, body);
    }

    public static HttpResponseBody ofFile(final File file) throws IOException {
        return new HttpResponseBody(ContentType.of(file), readFile(file));
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }
}
