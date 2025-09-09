package org.apache.coyote.util;

import org.apache.coyote.render.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class HttpResponseBuilder {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseBuilder.class);
    private static final String STATIC_FILE_ROOT = "static";


    public static String getStaticHttpResponse(final int statusCode, final String contentType, final String content) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + HttpStatus.getMessageByStatusCode(statusCode) + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content
        );
    }

    public static String getRedirectResponseString(final int statusCode, final String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + HttpStatus.getMessageByStatusCode(statusCode) + " ",
                "Location: " + location,
                "Content-Length: " + 0 + " ",
                "",
                ""
        );
    }

    public static String getErrorHttpResponse(final int statusCode) {
        try {
            String errorPageContent = readErrorPage(statusCode);
            return getStaticHttpResponse(statusCode, "text/html", errorPageContent);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("페이지를 불러오지 못했습니다.");
        }
    }

    private static String readErrorPage(int statusCode) throws IOException {
        String errorPagePath = STATIC_FILE_ROOT + "/" + statusCode + ".html";
        InputStream inputStream = HttpResponseBuilder.class.getClassLoader().getResourceAsStream(errorPagePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("에러 페이지를 찾을 수 없습니다: " + errorPagePath);
        }

        return new String(inputStream.readAllBytes());
    }

}