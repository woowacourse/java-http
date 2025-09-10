package org.apache.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpStatus;

public class StaticController implements Controller {

    @Override
    public boolean isProcessable(final String path) {
        return path.contains(".");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) throws IOException, URISyntaxException {
        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, Object> response = new HashMap<>();
        String filePath = requests.get("Path");

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);

        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        response.put("responseBody", new String(Files.readAllBytes(path)));
        response.put("status", httpStatus);

        return response;
    }
}
