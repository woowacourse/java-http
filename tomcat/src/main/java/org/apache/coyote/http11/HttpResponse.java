package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final String BLANK = " ";

    public static String getResponse(final HttpResponseEntity responseEntity) throws IOException {
        String contentType = "text/html;charset=utf-8";
        if (responseEntity.getPath().equals("/")) {
            return makeResponse(contentType, responseEntity, "Hello world!");
        }
        if (responseEntity.getPath().endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
        }

        final URL resource = HttpResponse.class.getClassLoader().getResource("static" + responseEntity.getPath());
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return makeResponse(contentType, responseEntity, responseBody);
    }

    private static String makeResponse(
            final String contentType,
            final HttpResponseEntity responseEntity,
            final String body
    ) {
        final HttpStatus status = responseEntity.getHttpStatus();
        if (responseEntity.hasCookie()) {
            return String.join("\r\n",
                    "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                    "Set-Cookie: " + responseEntity.getHttpCookie().getValues(),
                    "Content-Type: " + contentType + BLANK,
                    "Content-Length: " + body.getBytes().length + BLANK,
                    "",
                    body
            );
        }
        return String.join("\r\n",
                "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

}
