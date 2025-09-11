package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticResourceHandler {

    public static void handleStaticResource(String requestPath, HttpResponse response) throws IOException {
        URL resourceUrl = ClassLoader.getSystemResource("static" + requestPath);
        if (resourceUrl == null) {
            response.setStatusLine("HTTP/1.1", 404, "Not Found");
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody(new byte[0]);
            return;
        }

        byte[] fileBytes = Files.readAllBytes(Paths.get(resourceUrl.getPath()));
        String contentType = determineContentType(requestPath);

        response.setStatusLine("HTTP/1.1", 200, "OK");
        response.addHeader("Content-Type", contentType);
        response.setBody(fileBytes);
    }

    private static String determineContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        return "application/octet-stream";
    }
}
