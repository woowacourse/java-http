package org.apache.catalina.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

// RequestMapping에 등록된 경로와 매칭되지 않을 때 기본으로 사용되는 정적 리소스 처리 컨트롤러
public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String targetPath = request.getPath();
        if ("/".equals(targetPath)) {
            targetPath = "/index.html";
        } else if ("/login".equals(targetPath)) {
            targetPath = "/login.html";
        } else if ("/register".equals(targetPath)) {
            targetPath = "/register.html";
        }

        byte[] body;
        final String contentType;
        final var resourceUrl = getClass().getClassLoader().getResource("static" + targetPath);

        if (resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()))) {
            body = Files.readAllBytes(Path.of(resourceUrl.toURI()));
            if (targetPath.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            } else if (targetPath.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            } else {
                contentType = "text/html;charset=utf-8";
            }
        } else {
            body = "Hello world!".getBytes(StandardCharsets.UTF_8);
            contentType = "text/html;charset=utf-8";
        }

        response.writeBody(body, contentType);
    }
}
