package com.techcourse.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StaticResourceBody;

public class IndexPageHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return request.method() == HttpMethod.GET
                && (request.path().equals("/") || request.path().equals("/index.html"));
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) throws IOException {
        String html = new String(StaticResourceBody.from("/index.html").bytes(), StandardCharsets.UTF_8);
        if (request.getSession(false) != null) {
            html = html.replace("href=\"login\"", "href=\"/logout\"")
                    .replace("로그인", "로그아웃");
        }
        return HttpServletResponse.ok(
                new StaticResourceBody(html.getBytes(StandardCharsets.UTF_8), MimeType.HTML));
    }
}
