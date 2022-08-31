package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StaticResource {

    private final String content;
    private final String contentType;

    public StaticResource(final String content, final String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static StaticResource path(final String path) throws IOException {
        if (path.equals("/")) {
            return new StaticResource("Hello world!", "html");
        }
        final var resource = StaticResource.class.getClassLoader().getResource("static" + path);
        final var content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var type = "text/" + path.split("\\.")[1];
        return new StaticResource(content, type);
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        if (contentType.equals("css")) {
            return "text/css; charset=utf-8";
        }
        if (contentType.equals("js")) {
            return "application/javascript; charset=utf-8";
        }
        return "text/html; charset=utf-8";
    }

    public String getContentLength() {
        return String.valueOf(content.getBytes().length);
    }
}
