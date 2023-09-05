package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String BLANK = " ";

    private HttpResponse() {
    }

    public static String getResponse(final HttpResponseEntity responseEntity) throws IOException {
        final String contentType = ContentType.getByPath(responseEntity.getPath()) + CHARSET_UTF_8;
        if (responseEntity.getPath().equals("/")) {
            return makeResponse(contentType, responseEntity, "Hello world!");
        }
        if (responseEntity.isRedirect()) {
            if (responseEntity.hasCookie()) {
                return makeRedirectResponseWithCookie(responseEntity, responseEntity.getHttpCookie());
            }
            return makeRedirectResponse(responseEntity);
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
        if (responseEntity.hasCookie()) {
            return makeGeneralResponseWithCookie(contentType, responseEntity, body, responseEntity.getHttpCookie());
        }
        return makeGeneralResponse(contentType, responseEntity, body);
    }

    private static String makeRedirectResponse(final HttpResponseEntity responseEntity) {
        final HttpStatus status = responseEntity.getHttpStatus();
        return String.join("\r\n",
                "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Location: " + responseEntity.getPath() + BLANK,
                ""
        );
    }

    private static String makeRedirectResponseWithCookie(
            final HttpResponseEntity responseEntity,
            final HttpCookie cookie
    ) {
        final HttpStatus status = responseEntity.getHttpStatus();
        return String.join("\r\n",
                "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Set-Cookie: " + cookie.getValues() + BLANK,
                "Location: " + responseEntity.getPath() + BLANK,
                ""
        );
    }

    private static String makeGeneralResponseWithCookie(
            final String contentType,
            final HttpResponseEntity responseEntity,
            final String body,
            final HttpCookie cookie
    ) {
        final HttpStatus status = responseEntity.getHttpStatus();
        return String.join("\r\n",
                "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Set-Cookie: " + cookie.getValues() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

    private static String makeGeneralResponse(
            final String contentType,
            final HttpResponseEntity responseEntity,
            final String body
    ) {
        final HttpStatus status = responseEntity.getHttpStatus();
        return String.join("\r\n",
                "HTTP/1.1" + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }
}
