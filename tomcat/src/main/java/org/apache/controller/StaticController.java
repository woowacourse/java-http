package org.apache.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

public class StaticController implements Controller {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".html", ".css", ".js", ".ico");

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return ALLOWED_EXTENSIONS.stream().anyMatch(path::contains);
    }

    @Override
    public Map<String, Object> process(HttpRequest httpRequest) throws IOException, URISyntaxException {
        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, Object> response = new HashMap<>();

        final ClassLoader classLoader = getClass().getClassLoader();
        System.out.println(httpRequest.getStaticFilePath());
        final URL url = classLoader.getResource(httpRequest.getStaticFilePath());

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
