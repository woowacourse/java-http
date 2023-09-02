package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Handler {

    private Handler() {
    }

    public static Response handle(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        String resource = findResourceWithPath(requestURI.absolutePath());
        String contentType = ContentTypeParser.parse(requestURI.absolutePath());
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 OK",
                contentType, contentLength, resource);
    }

    private static String findResourceWithPath(String absolutePath) throws IOException {
        if (absolutePath.equals("/")) {
            return "Hello world!";
        }
        if (hasNoExtension(absolutePath)) {
            absolutePath += ".html";
        }

        URL resourceUrl = Handler.class.getClassLoader().getResource("static" + absolutePath);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }

    private static boolean hasNoExtension(String absolutePath) {
        return !absolutePath.contains(".");
    }
}
