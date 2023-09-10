package org.apache.coyote.controller;

import static org.apache.coyote.http11.HttpStatus.OK;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Utils;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(OK);
        if (request.getPath().equals("/")) {
            response.setContentType("text/html");
            response.setBody("Hello world!");
            return;
        }

        if (request.getFileName().endsWith(".html")) {
            response.setContentType("text/html");
            response.setBody(Utils.readFile("static", request.getFileName()));
            return;
        }

        if (request.getFileName().equals("styles.css")) {
            response.setContentType("text/css");
            response.setBody(Utils.readFile("static/css", request.getFileName()));
            return;
        }

        if (request.getFileName().endsWith(".js") && !request.getFileName().equals("scripts.js")) {
            response.setContentType("text/javascript");
            response.setBody(Utils.readFile("static/assets", request.getFileName()));
            return;
        }

        if (request.getFileName().equals("scripts.js")) {
            response.setContentType("text/javascript");
            response.setBody(Utils.readFile("static/js", request.getFileName()));
            return;
        }

        if (request.getFileName().equals("favicon.ico")) {
            response.setContentType("text/javascript");
            response.setBody("Hello world!");
            return;
        }

        throw new IllegalArgumentException("해당 리소스가 존재하지 않습니다.");

    }


}
