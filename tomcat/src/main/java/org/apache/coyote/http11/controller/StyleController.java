package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.util.FileReader;

public class StyleController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IOException(ExceptionMessage.NOT_SUPPORT_HTTP_METHOD.getMessage());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String body = new FileReader().readFile("static/css/styles.css");

        response.setContentType(ContentType.TEXT_CSS);
        response.setHttpBody(new HttpBody(body));
        response.send();
    }
}
