package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceHandler {

    public void serve(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws IOException {
        final String filePath = processFilePath(httpRequest.uri());
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("static/" + filePath)) {
            if (fileInputStream == null) {
                serveErrorPage(httpResponse, HttpStatus.NOT_FOUND);
                return;
            }
            byte[] body = fileInputStream.readAllBytes();
            httpResponse.setHeader("Content-Type", ContentType.fromPath(filePath).getMimeType());
            httpResponse.setBody(body);
        }
    }

    public void serveErrorPage(
            HttpResponse httpResponse,
            HttpStatus httpStatus
    ) throws IOException {
        String fileName = ErrorPage.getFileName(httpStatus);
        try (InputStream errorStream = getClass().getClassLoader().getResourceAsStream("static/" + fileName)) {
            if (errorStream != null) {
                byte[] errorBody = errorStream.readAllBytes();
                httpResponse.setStatusCode(httpStatus);
                httpResponse.setHeader("Content-Type", ContentType.HTML.getMimeType());
                httpResponse.setBody(errorBody);
            } else {
                httpResponse.setStatusCode(HttpStatus.NOT_FOUND);
                httpResponse.setHeader("Content-Type", ContentType.PLAIN.getMimeType());
                httpResponse.setBody(HttpResponse.bytes("Error page not found"));
            }
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
}
