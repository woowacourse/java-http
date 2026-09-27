package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceHandler {

    public void handle(final String path, final HttpResponse response) throws IOException {
        final String body = readResource(path, response);
        response.addBody(body);
        if (!response.hasStatusLine()) {
            response.addStatusLine(StatusLine.http11(HttpStatus.OK));
        }
        response.addHeader("Content-Type", getContentType(path));
        response.addHeader("Content-Length", String.valueOf(getContentLength(body)));
    }

    private String readResource(final String path, final HttpResponse response) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        final String staticResourceTarget = "/static" + path;

        final URL resource = getClass().getResource(staticResourceTarget);
        if (resource == null) {
            response.addStatusLine(StatusLine.http11(HttpStatus.NOT_FOUND));
            return readNotFound();
        }

        return new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
    }

    private String readNotFound() throws IOException {
        final URL notFoundResource = getClass().getResource("/static/404.html");

        if (notFoundResource == null) {
            throw new RuntimeException("404.html이 존재하지 않습니다.");
        }
        return new String(Files.readAllBytes(new File(notFoundResource.getPath()).toPath()));
    }

    private String getContentType(final String filePath) {
        final String charsetSuffix = ";charset=utf-8";
        final String defaultContentType = "text/html";
        if (filePath.equals("/")) {
            return defaultContentType + charsetSuffix;
        }
        final String prefix = "text/";
        final int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + filePath.substring(lastDotIndex + 1) + charsetSuffix;
    }

    private int getContentLength(final String body) {
        return body.getBytes().length;
    }


}
