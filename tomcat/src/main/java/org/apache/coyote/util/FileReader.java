package org.apache.coyote.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.apache.coyote.exception.ResourceNotFoundException;

public class FileReader {

    private static final String SYSTEM_DIRECTORY_KEY = "user.dir";
    private static final String RESOURCE_DIRECTORY = "tomcat/build/resources";

    private FileReader() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static Path parseFilePath(String fileName) {

        Path dirPath = Paths.get(System.getProperty(SYSTEM_DIRECTORY_KEY), RESOURCE_DIRECTORY);

        try (Stream<Path> walk = Files.walk(dirPath)) {
            Path filePath = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(fileName));

            return filePath.toAbsolutePath();
        } catch (IOException e) {
            throw new ResourceNotFoundException(fileName);
        }
    }

    public static List<String> readAllLines(Path filePath) throws IOException {
        List<String> contentLines = Files.readAllLines(filePath);
        contentLines.add("");
        return contentLines;
    }
}
