package com.techcourse.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StaticResourceBody;

public class IndexController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        String html = new String(StaticResourceBody.from("/index.html").bytes(), StandardCharsets.UTF_8);
        if (request.getSession(false) != null) {
            html = html.replace("href=\"login\"", "href=\"/logout\"")
                    .replace("로그인", "로그아웃");
        }
        return HttpResponse.ok(
                new StaticResourceBody(html.getBytes(StandardCharsets.UTF_8), MimeType.HTML));
    }
}
