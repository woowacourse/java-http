package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceHandler {

    public void serveStatic(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) throws IOException {
        final String filePath = processFilePath(httpRequest.getUri());
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("static/" + filePath)) {
            if (fileInputStream == null) {
                httpResponse.sendResponse(new byte[0], ContentType.HTML, HttpStatus.NOT_FOUND);
                return;
            }
            byte[] body = fileInputStream.readAllBytes();
            httpResponse.sendResponse(body, getContentType(filePath), HttpStatus.OK);
        }
    }

    private String processFilePath(String filePath) {
        if (filePath == null || filePath.isBlank() || "/".equals(filePath)) {
            return "index.html";
        }
        if (!filePath.contains(".")) {
            return filePath + ".html";
        }
        return filePath;
    }

    private ContentType getContentType(final String filePath) throws IOException {
        if (filePath.endsWith(".html")) {
            return ContentType.HTML;
        }
        if (filePath.endsWith(".css")) {
            return ContentType.CSS;
        }
        if (filePath.endsWith(".js")) {
            return ContentType.JAVASCRIPT;
        }
        throw new IOException("지원하지 않는 파일 형식입니다.");
    }
}
