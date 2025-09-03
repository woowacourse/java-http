package org.apache.coyote.http11;

import com.techcourse.Application;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.util.FileReader;

public class ResponseGenerator {

    private ResponseGenerator() {

    }

    public static String generateResponse(HttpRequest httpRequest) {
        if (httpRequest.isStaticResource()) {
            final HttpStatusCode statusCode = HttpStatusCode.OK;
            final ContentType contentType = ContentType.from(httpRequest.extractContentExpansion());
            final String body = readFile(httpRequest.resourcePath());
            return String.join("\r\n",
                    String.format("HTTP/1.1 %s %s ", statusCode.code, statusCode.message),
                    String.format("Content-Type: %s;charset=utf-8 ", contentType.responseContentType),
                    String.format("Content-Length: %d ", body.length()),
                    "",
                    body
            );
        }
        return "";
    }

    private static String readFile(String path) {
        final String resourcePath = String.format("static/%s", path);
        try (InputStream resourceAsStream = Application.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return FileReader.readFileString(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
