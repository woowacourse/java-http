package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.exception.RequestProcessingException;
import org.apache.http.ContentType;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;

public class StaticFileController implements Controller {

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        URL resource = findResourceUrl(request.getUri());
        return resource != null;
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        try {
            URL resource = findResourceUrl(request.getUri());
            if (resource == null) {
                throw new RequestProcessingException("URI가 올바르지 않습니다.");
            }

            Path path = Paths.get(resource.toURI());
            String responseBody = Files.readString(path);

            response.setStatusCode(StatusCode.OK);
            response.setHeader("Content-Type", getFileExtension(path).getValue());
            response.setBody(responseBody);

        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new RequestProcessingException("리소스를 읽는데 오류가 발생했습니다.");
        }
    }

    private URL findResourceUrl(String uri) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource("static" + uri);
    }

    private ContentType getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        List<String> split = List.of(fileName.split("\\."));
        return ContentType.parse(split.getLast());
    }
}
