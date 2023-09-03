package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final String BLANK = " ";

    public static String getResponse(final String path, final String protocolVersion) throws IOException {
        if (path.equals("/")) {
            return makeResponse(protocolVersion, HttpStatus.OK, "Hello world!");
        }

        final URL resource = HttpResponse.class.getClassLoader().getResource("static" + path);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return makeResponse(protocolVersion, HttpStatus.OK, responseBody);
    }

    private static String makeResponse(final String protocolVersion, final HttpStatus status, final String body) {
        return String.join("\r\n",
                protocolVersion + BLANK + status.getCode() + BLANK + status.name() + BLANK,
                "Content-Type: " + "text/html;charset=utf-8" + BLANK,
                "Content-Length: " + body.getBytes().length + BLANK,
                "",
                body
        );
    }

}
