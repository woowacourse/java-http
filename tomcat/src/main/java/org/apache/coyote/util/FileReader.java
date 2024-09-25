package org.apache.coyote.util;

import com.techcourse.exception.ResourceNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {

    private FileReader() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static List<String> readAllLines(String fileName) {
        try {
            Path filePath = parseFilePath(fileName);
            List<String> contentLines = Files.readAllLines(filePath);
            contentLines.add("");
            return contentLines;
        } catch (IOException e) {
            throw new ResourceNotFoundException(fileName);
        }
    }

    private static Path parseFilePath(String fileName) {
        try {
            URL resourceURL = FileReader.class.getClassLoader().getResource("static/" + fileName);
            URI uri = resourceURL.toURI();
            Path filePath = Paths.get(uri);

            return filePath.toAbsolutePath();
        } catch (Exception e) {
            throw new ResourceNotFoundException(fileName);
        }
    }
}
