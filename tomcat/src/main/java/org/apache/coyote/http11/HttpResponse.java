package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final String BLANK = " ";

    public static String getResponse(final String path, final String protocolVersion) throws IOException {
        String contentType = "text/html;charset=utf-8";
        if (path.equals("/")) {
            return makeResponse(protocolVersion, contentType, HttpStatus.OK, "Hello world!");
        }
        if (path.endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
            final URL resource = HttpResponse.class.getClassLoader().getResource("static" + path);
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return makeResponse(protocolVersion, contentType, HttpStatus.OK, responseBody);
        }
        if (path.equals("/login")) {
            final URL resource = HttpResponse.class.getClassLoader().getResource("static" + "/login.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return makeResponse(protocolVersion, contentType, HttpStatus.OK, responseBody);
        }

        final URL resource = HttpResponse.class.getClassLoader().getResource("static" + path);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return makeResponse(protocolVersion, contentType, HttpStatus.OK, responseBody);
    }

    private static String makeResponse(
            final String protocolVersion,
            final String contentType,
            final HttpStatus status,
            final String body
    ) {
        return String.join("\r\n",
                protocolVersion + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Content-Type: " + contentType + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

}
