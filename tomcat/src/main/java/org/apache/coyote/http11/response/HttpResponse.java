package org.apache.coyote.http11.response;

import javassist.NotFoundException;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Headers;
import org.apache.coyote.http11.response.header.Status;
import org.apache.coyote.http11.util.ResourceFinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpResponse {

    private final Status status;
    private final Headers headers;
    private final String body;

    private HttpResponse(final ContentType contentType, final Status status, final Map<Header, String> headers,
                         final String body) {
        this.status = status;
        this.headers = Headers.of(contentType, body);
        this.headers.putAll(headers);
        this.body = body;
    }

    private HttpResponse(final ContentType contentType, final Status status, final String body) {
        this(contentType, status, Map.of(), body);
    }

    private HttpResponse(final Status status, final String body) {
        this(ContentType.HTML, status, Map.of(), body);
    }

    public static HttpResponse ok(final ContentType contentType, final Status status, final Map<Header, String> headers, final String body) {
        return new HttpResponse(contentType, status, headers, body);
    }

    public static HttpResponse ok(final String body) {
        return new HttpResponse(Status.OK, body);
    }

    public static HttpResponse unAuthorized(final String path) throws NotFoundException, IOException, URISyntaxException {
        return new HttpResponse(ContentType.of(path), Status.UNAUTHORIZED, ResourceFinder.getStaticResource(path));
    }

    public static HttpResponse okWithResource(final String path) throws IOException, URISyntaxException, NotFoundException {
        return new HttpResponse(ContentType.of(path), Status.OK, ResourceFinder.getStaticResource(path));
    }

    public void setCookie(final Cookie cookie) {
        headers.put(Header.SET_COOKIE, cookie.toString());
    }

    @Override
    public String toString() {
        return status.toString() +
                "\r\n" +
                headers.toString() +
                "\r\n\r\n" +
                body;
    }
}
