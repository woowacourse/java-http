package org.apache.coyote.http11.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private FileReader() {
    }

    public static String read(String resourcePath) {
        try {
            URL url = FileReader.class.getClassLoader().getResource("static" + resourcePath);
            Path path = new File(url.getFile()).toPath();
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new IllegalArgumentException("파일을 읽는 중 예외가 발생했습니다.");
        }
    }

}
