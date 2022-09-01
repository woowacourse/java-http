package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResponseHandler {

    private final String path;

    public ResponseHandler(final String path) {
        this.path = path;
    }

    public String getResponse() throws IOException {
        if (path.equals("/favicon.ico")) {
            return "";
        }

        if (path.equals("/")) {
            return "Hello world!";
        }

        final URL resource = getClass()
                .getClassLoader()
                .getResource("static" + path);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public ContentType getContentType() {
        if (path.contains(".")) {
            final String[] splitExtension = path.split("\\.");
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }
}
