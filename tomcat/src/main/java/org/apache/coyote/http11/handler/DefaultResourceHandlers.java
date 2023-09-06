package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class DefaultResourceHandlers implements ResourceHandler {

    private static final String PATH = "/";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return PATH.equals(httpRequest.getPath())
                && HttpMethod.GET == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, body);
        httpResponse.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return httpResponse;
    }
}
