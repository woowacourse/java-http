package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReader {

    private static final String ROOT_URI = "/";

    private static final String BASE_DIRECTORY = "static";

    private static final String DEFAULT_RESPONSE = "Hello world!";

    public String readFile(String path) throws URISyntaxException, IOException {
        URI requestUri = new URI(path);
        String responseBody = DEFAULT_RESPONSE;
        if (!requestUri.toString().equals(ROOT_URI)) {
            URL resource = getClass().getClassLoader().getResource(BASE_DIRECTORY + requestUri);
            Path filePath = Path.of(resource.toURI());
            StringBuilder stringBuilder = new StringBuilder();
            List<String> fileLines = Files.readAllLines(filePath);
            fileLines.forEach(line -> stringBuilder.append(line).append(System.lineSeparator()));
            responseBody = stringBuilder.toString();
        }
        return responseBody;
    }
}
