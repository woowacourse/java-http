package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.http11.exception.ServerException;

public class FileReader {

    private final ClassLoader classLoader = getClass().getClassLoader();

    public String read(final String fileResource) {
        final URL resource = classLoader.getResource("static" + fileResource);
        final String resourceFile = getFile(resource);
        final Path path = Paths.get(resourceFile);
        final List<String> fileLines = readFileLines(path);
        final StringBuilder stringBuilder = new StringBuilder();
        fileLines.forEach(fileLine -> stringBuilder.append(fileLine).append("\n"));
        return stringBuilder.toString();
    }

    private String getFile(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("경로가 올바르지 않습니다.");
        }
        return resource.getFile();
    }

    private List<String> readFileLines(final Path path) {
        try {
            return Files.readAllLines(path);
        } catch (final IOException e) {
            throw new ServerException("파일을 읽어오는데 실패했습니다.");
        }
    }
}
