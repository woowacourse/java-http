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
                httpResponse.setStatus(
                        HttpStatus.NOT_FOUND.getStatusCode(),
                        HttpStatus.NOT_FOUND.getReasonPhrase()
                );
                httpResponse.setHeader("Content-Type", ContentType.PLAIN.getMimeType());
                httpResponse.setBody(HttpResponse.bytes("Not Found"));
                return;
            }
            byte[] body = fileInputStream.readAllBytes();
            httpResponse.setHeader("Content-Type", ContentType.fromPath(filePath).getMimeType());
            httpResponse.setBody(body);
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
