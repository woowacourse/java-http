package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class DefaultResourceHandler extends AbstractResourceHandler {

    private static final String PATH = "/";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return PATH.equals(httpRequest.getPath());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String body = "Hello world!";
        response.setStatusCode(StatusCode.OK);
        response.setBody(body);
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }
}
