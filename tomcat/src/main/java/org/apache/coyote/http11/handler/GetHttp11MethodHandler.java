package org.apache.coyote.http11.handler;

import static org.apache.coyote.header.ContentType.CHARSET_UTF_8;
import static org.apache.coyote.header.ContentType.TEXT_CSS;
import static org.apache.coyote.header.ContentType.TEXT_HTML;
import static org.apache.coyote.header.HttpMethod.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.SessionManager;
import org.apache.coyote.header.HttpCookie;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.session.Session;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHttp11MethodHandler implements Http11MethodHandler {

    private static final Logger log = LoggerFactory.getLogger(GetHttp11MethodHandler.class);

    @Override
    public HttpMethod supportMethod() {
        return GET;
    }

    @Override
    public String handle(final String headers, final String payload) {
        String targetPath = RequestExtractor.extractTargetPath(headers);
        if (targetPath.equals("/")) {
            return defaultContent();
        }

        if (!targetPath.contains(".")) {
            targetPath += ".html";
        }
        return resourceContent(HttpCookie.from(headers), targetPath);
    }

    private String defaultContent() {
        final var responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String resourceContent(final HttpCookie cookie, final String targetPath) {
        String sessionId = cookie.getValue(" JSESSIONID");
        if (sessionId != null) {
            Session session = SessionManager.findSession(sessionId);
            if (session != null) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found",
                        "Location: index.html"); // FIX: 리다이렉션이 너무 많다며 정상적으로 응답 처리가 안되는 현상 수정
            }
        }

        URL resourcePath = getClass().getClassLoader().getResource("static" + targetPath);

        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(new File(resourcePath.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType(targetPath) + ";" + CHARSET_UTF_8 + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String contentType(final String targetPath) {
        if (targetPath.contains(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
