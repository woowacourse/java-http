package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReader {

    private static final String ROOT_URI = "/";

    private static final String BASE_DIRECTORY = "static";

    private static final String DEFAULT_RESPONSE = "Hello world!";

    private static final FileReader instance = new FileReader();

    private FileReader() {
    }

    public String readFile(String path) throws URISyntaxException, IOException {
        String responseBody = DEFAULT_RESPONSE;
        if (!path.equals(ROOT_URI)) {
            URL resource = findFile(path);
            Path filePath = Path.of(resource.toURI());
            StringBuilder stringBuilder = new StringBuilder();
            List<String> fileLines = Files.readAllLines(filePath);
            fileLines.forEach(line -> stringBuilder.append(line).append(System.lineSeparator()));
            responseBody = stringBuilder.toString();
        }
        return responseBody;
    }

    private URL findFile(String path) {
        if (path.equals("/login")) {
            path = path.concat(".html");
        }
        URL url = getClass().getClassLoader().getResource(BASE_DIRECTORY + path);
        if (url == null) {
            String message = BASE_DIRECTORY + path + "에 파일이 존재하지 않습니다.";
            throw new IllegalArgumentException(message);
        }
        return url;
    }

    public String getFileExtension(String path) {
        if (path.equals(ROOT_URI)) {
            return ".html";
        }
        String filePath = findFile(path).toString();
        int fileExtensionIndex = filePath.lastIndexOf(".");
        return filePath.substring(fileExtensionIndex);
    }

    public static FileReader getInstance() {
        return instance;
    }
}
