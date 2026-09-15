package study;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class File {

    public static URL findFilePath(String fileName) {
        URL resource = File.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("[ERROR] 파일을 찾을 수 없습니다.");
        }

        return resource;
    }

    public static List<String> readAllLines(Path filePath) {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] 파일을 읽을 수 없습니다.");
        }
    }
}
