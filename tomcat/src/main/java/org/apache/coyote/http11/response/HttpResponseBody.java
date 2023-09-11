package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpContentType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponseBody {
    private static final String CRLF = "\r\n";
    private static final String STATIC = "static";

    private final HttpContentType contentType;
    private final String body;

    private HttpResponseBody(final HttpContentType contentType, final String body) {
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponseBody empty() {
        return new HttpResponseBody(null, null);
    }

    public static HttpResponseBody from(final String path) {
        try {
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            final String filePath = classLoader.getResource(STATIC + path).getPath();
            final String fileContent = new String(Files.readAllBytes(Path.of(filePath)));
            return new HttpResponseBody(HttpContentType.from(filePath), String.join(CRLF, fileContent));
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("해당 경로에 파일이 존재하지 않습니다.");
        }
    }

    public HttpResponseBody home(final String content) {
        return new HttpResponseBody(HttpContentType.TEXT_HTML, content);
    }

    public HttpContentType getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public int getLength() {
        return body.getBytes().length;
    }
}
