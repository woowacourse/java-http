package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;

public class HtmlResponse extends HttpResponse {

    private HtmlResponse(final HttpStatus status,
                         final HttpHeaders unnecessaryHeaders,
                         final String responseBody) {
        super(ContentType.TEXT_HTML, status, unnecessaryHeaders, responseBody);
    }

    public static HttpResponse of(final HttpStatus status,
                                  final HttpHeaders unnecessaryHeaders,
                                  final String fileName) {
        final URL resource = HtmlResponse.class
                .getClassLoader()
                .getResource("static" + File.separator + fileName);
        if (resource == null) {
            throw new IllegalArgumentException("html 파일이 존재하지 않습니다.");
        }

        try {
            final String responseBody = new String(Files.readAllBytes(Path.of(resource.getPath())));
            return new HtmlResponse(status, unnecessaryHeaders, responseBody);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패");
        }
    }
}
