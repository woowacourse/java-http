package org.apache.catalina.controller;

import static org.apache.coyote.http11.Mime.HTML;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
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
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(request.extractMimeType());
        response.setBody(FileReader.readByName(uri));
    }
}
