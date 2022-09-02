package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public class HttpResponseBody {

    private final String bodyContext;
    private final int contentLength;

    public HttpResponseBody(final String bodyContext) {
        this.bodyContext = bodyContext;
        this.contentLength = bodyContext.getBytes().length;
    }

    public static HttpResponseBody of(final String fileName) {
        return new HttpResponseBody(bodyOf(fileName));
    }

    public static String bodyOf(final String fileName) {
        try {
            URL url = HttpResponseBody.class.getClassLoader().getResource(fileName);
            String file = Objects.requireNonNull(url).getFile();
            Path path = new File(file).toPath();
            return String.join(CRLF, new String(Files.readAllBytes(path)));
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 이름의 파일을 찾을 수 없습니다: " + fileName);
        }
    }

    public String getBodyContext() {
        return bodyContext;
    }

    public int getContentLength() {
        return contentLength;
    }
}
