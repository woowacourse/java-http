package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class RootResponseGenerator implements ResponseGenerator {

    private static final String ROOT_PAGE_REQUEST_URI = "/";
    private static final String ROOT_PAGE_RESPONSE_BODY = "Hello world!";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(ROOT_PAGE_REQUEST_URI) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(ROOT_PAGE_RESPONSE_BODY, ContentType.TEXT_HTML);
    }
}
