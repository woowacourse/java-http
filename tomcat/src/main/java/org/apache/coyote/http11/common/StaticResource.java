package org.apache.coyote.http11.common;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;

public class StaticResource {

    private final String content;
    private final MediaType mediaType;

    public StaticResource(final String content, final MediaType mediaType) {
        this.content = content;
        this.mediaType = mediaType;
    }

    public static StaticResource path(final String path) {
        final var content = readContent(path);
        final var extension = path.split("\\.")[1];
        return new StaticResource(content, MediaType.fromExtension(extension));
    }

    private static String readContent(final String path) {
        try {
            final var resource = loadResource(path);
            return new String(Files.readAllBytes(Paths.get(resource.getPath())));
        } catch (IOException e) {
            throw new UncheckedServletException("파일을 읽어오는데 실패했습니다.");
        }
    }

    private static URL loadResource(final String path) {
        final var resource = ClassLoader.getSystemResource("static" + path);
        if (resource == null) {
            throw new NotFoundException("존재하지 않는 컨텐츠입니다.");
        }
        return resource;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return mediaType.getType();
    }

    public String getContentLength() {
        return String.valueOf(content.getBytes().length);
    }
}
