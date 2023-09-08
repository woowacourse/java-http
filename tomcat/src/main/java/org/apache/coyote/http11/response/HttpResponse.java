package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.coyote.http11.resource.Cookies;

public class HttpResponse {

    protected OutputStream outputStream;

    protected String responseStatus;

    protected String contentType;

    protected String charSet;

    protected int contentLength;

    protected Cookies cookies = new Cookies();

    protected String responseBody;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public HttpResponse(final OutputStream outputStream, final String responseStatus, final String contentType,
                        final String charSet,
                        final int contentLength, final Cookies cookies, final String responseBody) {
        this.outputStream = outputStream;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.charSet = charSet;
        this.contentLength = contentLength;
        this.cookies = cookies;
        this.responseBody = responseBody;
    }

    protected HttpResponse(OutputStream outputStream, String responseStatus, String contentType, String charSet, String responseBody) {
        this.outputStream = outputStream;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.charSet = charSet;
        this.responseBody = responseBody;
        this.contentLength = this.responseBody != null ? this.responseBody.getBytes().length : 0;
    }

    public void flush() throws IOException {
        outputStream.write(HttpResponseParser.parseToBytes(this));
        outputStream.flush();
    }

    public void setResponseStatus(final String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public void setCharSet(final String charSet) {
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

    public static class Builder {
        private String responseStatus;

        private String contentType;

        private String charSet;

        private String responseBody;

        public Builder responseStatus(String responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public RedirectBuilder redirect(final String redirectUrl) {
            return new RedirectBuilder(redirectUrl);
        }

        public HttpResponse build(OutputStream outputStream) {
            return new HttpResponse(
                    outputStream,
                    this.responseStatus == null ? "200 OK" : this.responseStatus,
                    this.contentType == null ? "text/html" : this.contentType,
                    this.charSet == null ? "utf-8" : this.charSet,
                    this.responseBody == null ? "" : this.responseBody
            );
        }
    }

    public static class RedirectBuilder {
        protected RedirectBuilder(final String redirectUri) {
            this.redirectUri = redirectUri;
        }

        private final String redirectUri;

        public HttpResponse build(OutputStream outputStream) {
            return new HttpRedirectResponse(outputStream, redirectUri);
        }
    }
}
