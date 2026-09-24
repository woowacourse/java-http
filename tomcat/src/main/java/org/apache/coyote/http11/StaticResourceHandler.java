package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceHandler {

    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final String filePath = getFilePath(request, response);
        final String body = readResource(filePath);
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

    private String readResource(final String filePath) throws IOException {
        if (filePath.equals("/")) {
            return "Hello world!";
        }
        final String staticResourceTarget = "/static" + filePath;

        final URL resource = getClass().getResource(staticResourceTarget);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스가 존재하지 않습니다 (filePath: " + staticResourceTarget);
        }

        return new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
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
