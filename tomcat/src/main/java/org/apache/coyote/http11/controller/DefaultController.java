package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IOException(ExceptionMessage.NOT_SUPPORT_HTTP_METHOD.getMessage());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpBody(new HttpBody("Hello world!"));
        response.send();
    }
}
