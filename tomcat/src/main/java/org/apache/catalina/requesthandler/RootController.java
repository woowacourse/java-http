package org.apache.catalina.requesthandler;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseStatus;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        response.setResponseStatus(ResponseStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(body);
    }
}

