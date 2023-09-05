package org.apache.coyote.handle.handler;

import java.io.IOException;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HelloWorldHandler implements Handler {

    private static final String BODY = "Hello world!";

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.TEXT_HTML.getType());
        httpResponse.setContent(BODY);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    }
}
