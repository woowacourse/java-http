package org.apache.coyote.util;

import org.apache.coyote.exception.CoyoteIOException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceReader {

    private ResourceReader() {
    }

    private static final String RESOURCE = "static";

    public static String read(final String resourceUri) {
        try {
            final URL resourceUrl = ResourceReader.class.getClassLoader().getResource(RESOURCE + resourceUri);
            final Path resourcePath = new File(resourceUrl.getFile()).toPath();

            return Files.readString(resourcePath);
        } catch (NullPointerException | IOException e) {
            throw new CoyoteIOException("정적 파일을 읽는 도중에 오류가 발생하였습니다.");
        }
    }
}
