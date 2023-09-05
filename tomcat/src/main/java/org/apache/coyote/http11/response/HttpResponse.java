package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.resource.Cookies;

public class HttpResponse {

    protected OutputStream outputStream;

    protected String responseStatus;

    protected String contentType;

    protected String charSet;

    protected int contentLength;

    protected Cookies cookies = new Cookies();

    protected String responseBody;

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

    public void addCookie(String key, String value) {
        this.cookies.add(key, value);
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

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder charSet(String charSet) {
            this.charSet = charSet;
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
