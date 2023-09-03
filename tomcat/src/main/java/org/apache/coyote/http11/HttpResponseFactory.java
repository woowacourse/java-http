package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.util.FileFinder;

public class HttpResponseFactory {

    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String EMPTY_BODY = "";

    public static HttpResponse createStaticResourceHttpResponse(final HttpRequest httpRequest) throws IOException {
        final String body = FileFinder.readFile(httpRequest.getPath());

        if (httpRequest.containsAccept(CSS_CONTENT_TYPE)) {
            return new HttpResponse(CSS_CONTENT_TYPE, body);
        }

        return new HttpResponse(HTML_CONTENT_TYPE, body);
    }

    public static HttpResponse createRedirectHttpResponse(
        final HttpResponseStatusLine statusLine,
        final String location
    ) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addHeader(HttpHeaderName.LOCATION, location);

        return new HttpResponse(statusLine, httpHeaders, EMPTY_BODY);
    }

    public static HttpResponse createRedirectHttpResponse(
        final HttpResponseStatusLine statusLine,
        final String location,
        final HttpCookie httpCookie
    ) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addHeader(HttpHeaderName.LOCATION, location);
        httpHeaders.addHeader(HttpHeaderName.SET_COOKIE, httpCookie.toString());

        return new HttpResponse(statusLine, httpHeaders, EMPTY_BODY);
    }
}
