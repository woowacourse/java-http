package org.apache.coyote.http11.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.http11.util.HttpStatus;

public class HttpResponse {

    private static final String SPACE = " ";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final OutputStream outputStream;
    private ResourceURI resourceURI;
    private HttpStatus httpStatus;
    private Location location;
    private Cookies cookies;

    private HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static HttpResponse from(final OutputStream outputStream) {
        return new HttpResponse(outputStream);
    }

    private byte[] getBytes() throws IOException {
        return getHttpMessage().getBytes();
    }

    private String getHttpMessage() throws IOException {
        final var stringBuilder = new StringBuilder();
        appendStatusLine(stringBuilder);
        appendLocationLine(stringBuilder);
        appendCookieLine(stringBuilder);
        appendContent(stringBuilder);
        return stringBuilder.toString();
    }

    private void appendStatusLine(final StringBuilder stringBuilder) {
        stringBuilder.append(getStatusLine());
        stringBuilder.append(System.lineSeparator());
    }

    private String getStatusLine() {
        return HTTP_VERSION + SPACE + httpStatus.getValue() + SPACE;
    }

    private void appendLocationLine(final StringBuilder stringBuilder) {
        if (!Objects.isNull(location)) {
            stringBuilder.append(location.getLocationLine());
            stringBuilder.append(System.lineSeparator());
        }
    }

    private void appendCookieLine(final StringBuilder stringBuilder) {
        if (!Objects.isNull(cookies)) {
            stringBuilder.append(cookies.getCookieLine());
            stringBuilder.append(System.lineSeparator());
        }
    }

    private void appendContent(final StringBuilder stringBuilder) throws IOException {
        if (!Objects.isNull(resourceURI)) {
            stringBuilder.append(resourceURI.getContent());
        }
    }

    public void write() throws IOException {
        outputStream.write(getBytes());
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public void setResourceURI(final ResourceURI resourceURI) {
        this.resourceURI = resourceURI;
    }

    public void setStatusCode(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public void addCookie(final Cookies cookies) {
        this.cookies = cookies;
    }
}
