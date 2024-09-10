package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.resource.ResourceParser;

public class ResourceController implements Controller {

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        try {
            File requestFile = ResourceParser.getRequestFile(req);
            resp.setResponse("200 OK", requestFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 읽기/쓰기 과정에서 예외 발생 (Path: %s)".formatted(req.getPath()));
        }
    }
}
