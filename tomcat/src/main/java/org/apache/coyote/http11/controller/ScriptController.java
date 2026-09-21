package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.util.FileReader;

public class ScriptController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IOException(ExceptionMessage.NOT_SUPPORT_HTTP_METHOD.getMessage());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String body = new FileReader().readFile("static/js/scripts.js");

        response.setContentType(ContentType.TEXT_JAVASCRIPT);
        response.setHttpBody(new HttpBody(body));
        response.write();
    }
}
