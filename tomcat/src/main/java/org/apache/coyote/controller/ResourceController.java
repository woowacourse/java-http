package org.apache.coyote.controller;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Utils;

public class ResourceController extends AbstractController {

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(OK);

        String path = request.getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);

        if (fileName.endsWith(".html")) {
            response.setContentType("text/html");
            response.setBody(Utils.readFile("static", fileName));
            return;
        }

        if (fileName.equals("styles.css")) {
            response.setContentType("text/css");
            response.setBody(Utils.readFile("static/css", fileName));
            return;
        }

        if (fileName.endsWith(".js") && !fileName.equals("scripts.js")) {
            response.setContentType("text/javascript");
            response.setBody(Utils.readFile("static/assets", fileName));
            return;
        }

        if (fileName.equals("scripts.js")) {
            response.setContentType("text/javascript");
            response.setBody(Utils.readFile("static/js", fileName));
            return;
        }

        if (fileName.equals("favicon.ico")) {
            response.setContentType("text/javascript");
            response.setBody("Hello world!");
            return;
        }

        response.setStatus(FOUND);
        response.setRedirectUrl("404.html");

    }


}
