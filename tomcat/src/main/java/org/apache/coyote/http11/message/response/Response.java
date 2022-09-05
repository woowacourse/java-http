package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.http11.message.Regex;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.request.header.Cookie;
import org.apache.coyote.http11.message.response.header.ContentType;
import org.apache.coyote.http11.message.response.header.Headers;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class Response {

    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final StatusCode statusCode;
    private final Headers headers;
    private final String body;

    public Response(final ContentType contentType, final StatusCode statusCode, final Map<Header, String> headers,
                    final String body) {
        this.statusCode = statusCode;
        this.headers = Headers.of(contentType, body);
        this.headers.putAll(headers);
        this.body = body;
    }

    public Response(final ContentType contentType, final StatusCode statusCode, final String body) {
        this(contentType, statusCode, Map.of(), body);
    }

    public Response(final StatusCode statusCode, final String body) {
        this(ContentType.HTML, statusCode, Map.of(), body);
    }

    public static Response ofOk(final String body) {
        return new Response(StatusCode.OK, body);
    }

    public static Response ofRedirection(final StatusCode statusCode, final String location) {
        return new Response(ContentType.HTML, statusCode, Map.of(Header.LOCATION, location), "");
    }

    public static Response ofResource(final String path) throws IOException, URISyntaxException {
        return new Response(ContentType.of(path), StatusCode.OK, ResourceLoader.getStaticResource(path));
    }

    public void setCookie(final Cookie cookie) {
        headers.put(Header.SET_COOKIE, cookie.toText());
    }

    public String toText() {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.toString() + Regex.BLANK.getValue(),
                headers.toText(),
                "",
                body);
    }
}
