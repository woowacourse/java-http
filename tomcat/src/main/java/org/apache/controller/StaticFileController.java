package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.exception.ResourceNotFound;
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
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            response.setHttpVersion(request.getVersion());
            response.setStatusCode(StatusCode.OK);
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody(responseBody);
        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new ResourceNotFound("해당하는 리소스를 찾을 수 없습니다.");
        }
    }
}
