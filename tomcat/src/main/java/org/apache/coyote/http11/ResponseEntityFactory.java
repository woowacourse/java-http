package org.apache.coyote.http11;

import java.io.IOException;
import nextstep.jwp.controller.ResponseEntity;
import org.apache.coyote.util.FileFinder;

public class ResponseEntityFactory {

    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String EMPTY_BODY = "";

    private ResponseEntityFactory() {
    }

    public static ResponseEntity createStaticResourceHttpResponse(final HttpRequest httpRequest) throws IOException {
        final String body = FileFinder.readFile(httpRequest.getPath());

        if (httpRequest.containsAccept(CSS_CONTENT_TYPE)) {
            return new ResponseEntity(CSS_CONTENT_TYPE, body);
        }

        return new ResponseEntity(HTML_CONTENT_TYPE, body);
    }

    public static ResponseEntity createRedirectHttpResponse(
        final HttpResponseStatusLine statusLine,
        final StaticPages location
    ) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addHeader(HttpHeaderName.LOCATION, location.getFileName());

        return new ResponseEntity(statusLine, httpHeaders, EMPTY_BODY);
    }

    public static ResponseEntity createRedirectHttpResponse(
        final HttpResponseStatusLine statusLine,
        final StaticPages location,
        final HttpCookie httpCookie
    ) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addHeader(HttpHeaderName.LOCATION, location.getFileName());
        httpHeaders.addHeader(HttpHeaderName.SET_COOKIE, httpCookie.toString());

        return new ResponseEntity(statusLine, httpHeaders, EMPTY_BODY);
    }
}
