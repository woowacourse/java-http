package org.apache.catalina.controller;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final String fileName;
    private final String contentType;

    public StaticResourceController(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new FileNotFoundException("정적 파일을 찾을 수 없습니다: " + fileName);
        }

        Path filePath = Path.of(resource.toURI());
        String content = Files.readString(filePath, StandardCharsets.UTF_8);

        response.ok(contentType, content);
    }
}
