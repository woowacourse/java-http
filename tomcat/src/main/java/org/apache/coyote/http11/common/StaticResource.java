package org.apache.coyote.http11.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StaticResource {

    private final String content;
    private final ContentType contentType;

    public StaticResource(final String content, final ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static StaticResource path(final String path) throws IOException {
        final var resource = StaticResource.class.getClassLoader().getResource("static" + path);
        final var content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var extension = path.split("\\.")[1];
        return new StaticResource(content, ContentType.extension(extension));
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType.getType();
    }

    public String getContentLength() {
        return String.valueOf(content.getBytes().length);
    }
}
