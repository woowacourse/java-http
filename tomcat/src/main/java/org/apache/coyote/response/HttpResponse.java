package org.apache.coyote.response;

import static org.apache.coyote.response.ResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.response.ResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.response.ResponseHeader.LOCATION;
import static org.apache.coyote.response.ResponseHeader.SET_COOKIE;
import static org.apache.coyote.response.Status.OK;
import static org.apache.coyote.utils.Constant.EMPTY;
import static org.apache.coyote.utils.Constant.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.ContentType;
import org.apache.coyote.Cookies;
import org.apache.coyote.Headers;
import org.apache.coyote.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final StatusLine statusLine;
    private final Headers headers;
    private final Cookies cookies;
    private String body;

    private HttpResponse(final StatusLine statusLine, final Headers headers) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.cookies = new Cookies();
    }

    public static HttpResponse create(final Protocol protocol, final ContentType contentType) {
        final StatusLine statusLine = new StatusLine(protocol, OK);
        final Headers headers = new Headers();
        headers.addHeader(CONTENT_TYPE, contentType.getValueWithUTF8());

        return new HttpResponse(statusLine, headers);
    }

    public void setStatus(final Status status) {
        this.statusLine.setStatus(status);
    }

    public void setRedirectUri(final String redirectUri) {
        headers.addHeader(LOCATION, redirectUri);
    }

    public void addCookie(final String name, final String value) {
        cookies.addCookie(name, value);
    }

    public void setBody(final String text) {
        this.body = text;
        headers.addHeader(CONTENT_LENGTH, Integer.toString(body.getBytes().length));
    }

    public void setBody(final URL resource) {
        try {
            this.body = readResource(resource);
            headers.addHeader(CONTENT_LENGTH, Integer.toString(body.getBytes().length));
        } catch (final IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readResource(final URL resource) throws IOException {
        final Path filePath = new File(resource.getFile()).toPath();
        return new String(Files.readAllBytes(filePath));
    }

    public String stringify() {
        if (!cookies.isEmpty()) {
            headers.addHeader(SET_COOKIE, cookies.stringify());
        }

        return String.join(LINE_SEPARATOR,
                statusLine.stringify(),
                headers.stringify(),
                EMPTY,
                body);
    }
}
