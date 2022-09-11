package org.apache.coyote.http11.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;

public class Files {

    public static boolean existsFile(final String path) {
        final URL fileUrl = Files.class.getClassLoader().getResource("static" + path);
        return fileUrl != null;
    }

    public static String readFile(final String path) throws IOException {
        final URL fileUrl = Files.class.getClassLoader().getResource("static" + path);
        if (fileUrl == null) {
            throw new NoSuchFileException(path + " 파일이 없습니다.");
        }

        try (final FileInputStream fileInputStream = new FileInputStream(fileUrl.getPath())) {
            return new String(fileInputStream.readAllBytes());
        }
    }

}
