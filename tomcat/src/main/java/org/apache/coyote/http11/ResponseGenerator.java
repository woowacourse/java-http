package org.apache.coyote.http11;

import com.techcourse.Application;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatusCode;
import org.apache.coyote.util.StreamReader;

public class ResponseGenerator {

    private ResponseGenerator() {

    }

    public static String generateResponse(HttpRequest httpRequest) {
        if (httpRequest.isStaticResource()) {
            final HttpStatusCode statusCode = HttpStatusCode.OK;
            final ContentType contentType = ContentType.from(httpRequest.extractContentExpansion());
            final String body = readFile(httpRequest.resourcePath());
            return String.join("\r\n",
                    String.format("HTTP/1.1 %s %s ", statusCode.getCode(), statusCode.getMessage()),
                    String.format("Content-Type: %s;charset=utf-8 ", contentType.getResponseContentType()),
                    String.format("Content-Length: %d ", body.length()),
                    "",
                    body
            );
        } else {
            /*
                TODO: 정적파일이 아닌 경우 로직을 실행시키고 응답을 반환한다.
             */
        }
        return "";
    }

    private static String readFile(String path) {
        final String resourcePath = String.format("static/%s", path);
        try (InputStream resourceAsStream = Application.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return StreamReader.readAllLine(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
