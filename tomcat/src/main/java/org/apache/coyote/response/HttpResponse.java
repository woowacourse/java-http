package org.apache.coyote.response;

import static org.apache.coyote.response.ResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.response.ResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.response.ResponseHeader.LOCATION;
import static org.apache.coyote.response.ResponseHeader.SET_COOKIE;
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
import org.apache.coyote.exception.PageNotFoundException;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final StatusLine statusLine, final Headers headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder(final Protocol protocol, final ContentType contentType, final Status status) {
        return new Builder(protocol, contentType, status);
    }

    public String stringify() {
        return String.join(LINE_SEPARATOR,
                statusLine.stringify(),
                headers.stringify(),
                EMPTY,
                body);
    }

    public static class Builder {
        private final Protocol protocol;
        private final ContentType contentType;
        private final Status status;
        private String redirectUri;
        private Cookies cookies;
        private String body;

        public Builder(final Protocol protocol, final ContentType contentType, final Status status) {
            this.protocol = protocol;
            this.contentType = contentType;
            this.status = status;
        }

        private static String readResource(final URL resource) {
            try {
                final Path filePath = new File(resource.getFile()).toPath();
                return new String(Files.readAllBytes(filePath));

            } catch (final NullPointerException | IOException e) {
                throw new PageNotFoundException(resource.toString());
            }
        }

        public Builder redirectUri(final String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder cookies(final Cookies cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder body(final URL resource) {
            this.body = readResource(resource);
            return this;
        }

        public HttpResponse build() {
            final StatusLine statusLine = new StatusLine(protocol, status);
            final Headers headers = new Headers();
            headers.addHeader(CONTENT_TYPE, contentType.getValueWithUTF8());

            if (redirectUri != null) {
                headers.addHeader(LOCATION, redirectUri);
            }

            if (cookies != null) {
                headers.addHeader(SET_COOKIE, cookies.stringify());
            }

            if (body == null) {
                body = EMPTY;
            }

            headers.addHeader(CONTENT_LENGTH, body.getBytes().length + " ");

            return new HttpResponse(statusLine, headers, body);
        }
    }

}
