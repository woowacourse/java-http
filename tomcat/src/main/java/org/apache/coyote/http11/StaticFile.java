package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticFile {
    private final String path;

    public StaticFile(final String path) {
        this.path = path;
    }

    public String getBody() {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final URL url = classLoader.getResource("static/" + path);
        try {
            return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
        } catch (final NullPointerException e) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        } catch (final IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }

    public String getExtension() {
        final int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return path.substring(lastDotIndex);
        }
        throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다.");
    }
}
