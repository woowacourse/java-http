package com.techcourse.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StaticResourceBody;

public class IndexPageHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.GET
                && (request.path().equals("/") || request.path().equals("/index.html"));
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String html = new String(StaticResourceBody.from("/index.html").bytes(), StandardCharsets.UTF_8);
        if (request.getSession(false) != null) {
            html = html.replace("href=\"login\"", "href=\"/logout\"")
                    .replace("로그인", "로그아웃");
        }
        return HttpResponse.ok(
                new StaticResourceBody(html.getBytes(StandardCharsets.UTF_8), MimeType.HTML));
    }
}
