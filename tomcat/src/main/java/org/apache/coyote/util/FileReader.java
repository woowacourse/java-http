package org.apache.coyote.util;

import com.techcourse.exception.ResourceNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FileReader {

    private FileReader() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static Path parseFilePath(String fileName) throws URISyntaxException {

        URL resourceURL = FileReader.class.getClassLoader().getResource("static/" + fileName);
        if (Objects.isNull(resourceURL)) {
            throw new ResourceNotFoundException(fileName);
        }
        URI uri = resourceURL.toURI();
        Path filePath = Paths.get(uri);

        return filePath.toAbsolutePath();
    }

    public static List<String> readAllLines(Path filePath) throws IOException {
        List<String> contentLines = Files.readAllLines(filePath);
        contentLines.add("");
        return contentLines;
    }
}
