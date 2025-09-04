package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.exception.ResourceNotFound;
import org.apache.http.ContentType;
import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;
import org.apache.http.StatusCode;

public class StaticFileController {

    public void processResourceRequest(HttpRequestMessage request, HttpResponseMessage response) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("static" + request.getUri());
            if (resource == null) {
                throw new IllegalArgumentException("URI가 올바르지 않습니다.");
            }
            Path path = Paths.get(resource.toURI());
            String responseBody = Files.readString(path);
            response.setHttpVersion(request.getVersion());
            response.setStatusCode(StatusCode.OK);
            response.setHeader("Content-Type", getFileExtension(path).getValue());
            response.setBody(responseBody);
        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new ResourceNotFound("해당하는 리소스를 찾을 수 없습니다.");
        }
    }

    private ContentType getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        List<String> split = List.of(fileName.split("\\."));
        return ContentType.parse(split.getLast());
    }
}
