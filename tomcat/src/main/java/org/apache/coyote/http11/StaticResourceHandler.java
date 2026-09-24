package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceHandler {

    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final String filePath = getFilePath(request, response);
        final String body = readResource(request, response);
        response.addBody(body);
        if (!response.hasStatusLine()) {
            response.addStatusLine(StatusLine.http11(HttpStatus.OK));
        }
        response.addHeader("Content-Type", getContentType(filePath));
        response.addHeader("Content-Length", String.valueOf(getContentLength(body)));
    }

    private String getFilePath(final HttpRequest request, final HttpResponse response) {
        if (response.hasForwardPath()) {
            return response.forwardPath();
        }
        return request.path();
    }

    private String readResource(final HttpRequest request, final HttpResponse response) throws IOException {
        final String filePath = getFilePath(request, response);
        if (filePath.equals("/")) {
            return "Hello world!";
        }
        final String staticResourceTarget = "/static" + filePath;

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
