package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.exception.ResourceNotFound;
import org.apache.http.HttpRequestMessage;

public class StaticFileController {

    public String processResourceRequest(HttpRequestMessage request) {
        try {
            System.out.println("request.getUri() = " + request.getUri());
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("static" + request.getUri());
            if (resource == null) {
                throw new IllegalArgumentException("URI가 올바르지 않습니다.");
            }
            Path path = Paths.get(resource.toURI());
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new ResourceNotFound("해당하는 리소스를 찾을 수 없습니다.");
        }
    }
}
