package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URI;
import org.apache.coyote.http11.request.HttpProtocol;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class GreetingHandler implements HttpRequestHandler {

    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final URI SUPPORTING_URI = URI.create("");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;


    @Override
    public boolean supports(final HttpRequest request) {
        if (request.isMethodNotEqualWith(SUPPORTING_METHOD)) {
            return false;
        }
        if (request.isHttpProtocolNotEqualWith(SUPPORTING_PROTOCOL)) {
            return false;
        }
        if (request.isUriNotEqualWith(SUPPORTING_URI)) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        return HttpResponse.ok("Hello world!", "html");
    }
}
