package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE = "%s %s %s" + CRLF;
    private static final String END_OF_HEADER = CRLF;

    private final OutputStream outputStream;

    private HttpProtocol protocol;
    private HttpStatusCode statusCode;
    private final HttpResponseHeader responseHeader;
    private final HttpResponseBody body;
    private MimeType mimeType;
    private final HttpCookie cookie;


    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.responseHeader = new HttpResponseHeader();
        this.body = new HttpResponseBody();
        this.cookie = new HttpCookie();
    }

    public void addCookie(String cookieName, String cookieValue) {
        cookie.addCookie(cookieName, cookieValue);
    }

    public void addHeader(String name, String value) {
        responseHeader.addHeader(name, value);
    }

    public void write(byte[] data) throws IOException {
        body.write(new String(data, StandardCharsets.UTF_8));
    }

    public void sendRedirect(String location) throws IOException {
        setLocation(location);
        send();
    }

    public void send() throws IOException {
        if (body.isNotEmpty()) {
            setContentHeader();
        }
        outputStream.write(asString().getBytes());
        outputStream.flush();
    }

    private void setContentHeader() {
        responseHeader.addHeader("Content-Type", mimeType.getContentType());
        responseHeader.addHeader("Content-Length", String.valueOf(body.getLength()));
    }

    private void setLocation(String location) {
        responseHeader.addHeader("Location", location);
    }

    private String asString() {
        StringBuilder builder =  new StringBuilder();
        builder.append(String.format(RESPONSE_LINE, protocol.getVersion(), statusCode.getCode(), statusCode.getReasonPhrase()));
        builder.append(responseHeader.asString());
        if (!cookie.isEmpty()) {
            String setCookie = cookie.getSetCookie();
            builder.append(setCookie);
        }
        builder.append(END_OF_HEADER);
        builder.append(body.asString());
        return builder.toString();
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setProtocol(HttpProtocol protocol) {
        this.protocol = protocol;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }
}
