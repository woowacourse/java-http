package org.apache.coyote.http11.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StaticResource;

public class Http11Response {

    private StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseCookies responseCookies;
    private byte[] responseBody;

    public Http11Response() {
        this.statusLine = new StatusLine(HttpStatus.INTERNAL_SERVER_ERROR);
        this.responseHeaders = new ResponseHeaders();
        this.responseCookies = new ResponseCookies();
        this.responseBody = new byte[0];
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.statusLine = new StatusLine(httpStatus);
    }

    public void addHeader(final String name, final String value) {
        responseHeaders.addHeader(name, value);
    }

    public void addCookie(final String cookieName, final String cookieValue) {
        responseCookies.addCookie(cookieName, cookieValue);
    }

    public void setStaticResource(final StaticResource staticResource) {
        addHeader("Content-Type", staticResource.getMimeType());
        addHeader("Content-Length", String.valueOf(staticResource.getContentLength()));
        setBody(staticResource.getContent());
    }

    public void setBody(final byte[] body) {
        if (body == null) {
            this.responseBody = new byte[0];
            return;
        }

        this.responseBody = new byte[body.length];
        System.arraycopy(body, 0, this.responseBody, 0, body.length);
    }

    public byte[] toResponseBytes() throws IOException {
        final String responseStatusLineMessage = statusLine.convertResponseStatusLineMessage();
        final String responseHeadersMessage = responseHeaders.convertResponseHeadersMessage();
        final String responseCookiesMessage = responseCookies.convertResponseCookiesMessage();
        final String blankLine = "\r\n";

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // header byte
            outputStream.write(responseStatusLineMessage.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseHeadersMessage.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseCookiesMessage.getBytes(StandardCharsets.UTF_8));
            outputStream.write(blankLine.getBytes(StandardCharsets.UTF_8));

            // body byte
            outputStream.write(responseBody);

            return outputStream.toByteArray();
        }
    }
}
