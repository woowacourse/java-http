package org.apache.coyote.http11.controller;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, Http11Response response) throws Exception {
        String path = request.getRequestPath();
        URL resource = StaticResourceController.class.getClassLoader()
                .getResource("static" + path);

        response.setBody(Files.readAllBytes(Path.of(resource.toURI())));
        response.ok(findContentType(path));
    }

    private String findContentType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        }
        if (url.endsWith(".css")) {
            return "text/css";
        }
        if (url.endsWith(".js")) {
            return "application/javascript";
        }
        throw new IllegalArgumentException("지원하지 않는 파일 형식: " + url);
    }
}
