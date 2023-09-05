package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RegisterGetResponseMaker extends ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) throws IOException {
        final String resourcePath = request.getRequestLine().getRequestUrl() + ".html";
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, ContentType.from(resourcePath), new String(getResponseBodyBytes(resourcePath), UTF_8));
        return httpResponse.getResponse();
    }

}
