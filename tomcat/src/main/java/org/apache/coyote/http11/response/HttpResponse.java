package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.http11.resource.CharSet;
import org.apache.coyote.http11.resource.ContentType;
import org.apache.coyote.http11.resource.Cookies;
import org.apache.coyote.http11.resource.ResponseStatus;

public class HttpResponse {

    protected OutputStream outputStream;

    protected ResponseStatus responseStatus;

    protected ContentType contentType;

    protected CharSet charSet;

    protected int contentLength;

    protected Cookies cookies = new Cookies();

    protected String responseBody;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    protected HttpResponse(final OutputStream outputStream, final ResponseStatus responseStatus, final ContentType contentType,
                        final CharSet charSet,
                        final int contentLength, final Cookies cookies, final String responseBody) {
        this.outputStream = outputStream;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.charSet = charSet;
        this.contentLength = contentLength;
        this.cookies = cookies;
        this.responseBody = responseBody;
    }

    public void flush() throws IOException {
        outputStream.write(HttpResponseParser.parseToBytes(this));
        outputStream.flush();
    }

    public void setResponseStatus(final ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public void setCharSet(final CharSet charSet) {
        this.charSet = charSet;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
        if (Objects.isNull(responseBody)) {
            this.contentLength = 0;
            return;
        }
        this.contentLength = responseBody.getBytes().length;
    }

    public void addCookie(String key, String value) {
        this.cookies.add(key, value);
    }

    public void sendRedirect(final String location) throws IOException {
        new HttpRedirectResponse(outputStream, cookies, location).flush();
    }
}
