package org.apache.coyote;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticRenderer {

    public void redirectToIndexPage(Response response) throws IOException {
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.addHeader("Location", "/index.html");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", response.getContentLength());
        response.send();
    }

    public void renderStaticPage(Request request, Response response) throws Exception {
        // 필요 시 로그인 HTML 페이지 반환
        Path path = Paths.get(request.getResourcePath());
        response.addHeader("Content-Type", getContentType(path));
        response.setBody(getStaticResource(path));
        response.addHeader("Content-Length", response.getContentLength());
        response.send();
    }

    private String getContentType(Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "text/html";
        }
        return contentType + ";charset=utf-8";
    }

    private String getStaticResource(Path path) throws IOException, URISyntaxException {
        if (path.equals(Path.of("\\"))) {
            return "Hello world!";
        }
        Path staticPath = getStaticPath(path);
        return new String(Files.readAllBytes(staticPath));
    }

    private Path getStaticPath(Path path) throws URISyntaxException {
        if (!path.toString().contains(".")) {
            path = Path.of(path + ".html");
        }
        return Paths.get(getClass().getClassLoader().getResource("static" + path).toURI());
    }
}
