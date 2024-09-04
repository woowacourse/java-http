package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStateCode;

public class FrontController {

    public FrontController() {
    }

    public HttpResponse dispatch(HttpRequest request) {
        if (request.isFileRequest()) {
            String path = request.getPath();
            System.out.println(path);
            String resourcePath = "static" + path;
            try {
                Path filePath = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
                byte[] body = Files.readAllBytes(filePath);
                return new HttpResponse(HttpStateCode.OK, body);
            } catch (URISyntaxException | IOException e) {
                return new HttpResponse(HttpStateCode.OK, "No File Found".getBytes());
            }
        }
        return new HttpResponse(HttpStateCode.OK, "Hello world!".getBytes());
    }
}
