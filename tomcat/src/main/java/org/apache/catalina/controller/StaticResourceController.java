package org.apache.catalina.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String path = request.getPath();
        response.ok(resolveContentType(path), readStaticFile(path));
    }

    private String readStaticFile(final String path) throws URISyntaxException, IOException {
        final String fileName = "static" + resolveFileName(path);
        final URL url = ClassLoader.getSystemResource(fileName); // 클래스패스에서 static/ 아래 파일을 찾아 실제 위치를 URL로 돌려 줌
        final File file = new File(url.toURI());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    // 요청 path를 받아서, 서버에서 찾을 파일 이름을 돌려 줌
    private String resolveFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
