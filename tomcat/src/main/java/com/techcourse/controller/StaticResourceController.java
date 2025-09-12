package com.techcourse.controller;

import static org.apache.catalina.http.vo.Mime.HTML;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.vo.HttpStatus;
import org.apache.coyote.http11.util.FileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws FileNotFoundException, IOException {
        final var uri = request.getURI();
        if (uri.equals("/")) {
            response.setStatus(HttpStatus.OK);
            response.setContentType(HTML.getType());
            response.setBody("Hello world!");
            response.send();
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(request.extractMimeType());
        response.setBody(FileReader.readByName(uri));
        response.send();
    }
}
