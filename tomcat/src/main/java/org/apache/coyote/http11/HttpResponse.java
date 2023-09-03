package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;
    private final String contentType;
    private final String charset;
    private final int contentLength;
    private final byte[] body;

    public HttpResponse(HttpVersion httpVersion, HttpStatusCode httpStatusCode, String contentType,
            String charset, byte[] body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.charset = charset;
        this.contentLength = body.length;
        this.body = body;
    }

    public void addToOutputStream(OutputStream outputStream) throws IOException {
        addHeaders(outputStream);
        addBody(outputStream);
    }

    private void addHeaders(OutputStream outputStream) throws IOException {
        outputStream.write(String.join("\r\n",
                "HTTP/" + httpVersion.getVersion() + " " + httpStatusCode.toResponseFormat() + " ",
                "Content-Type: " + contentType + ";charset=" + charset + " ",
                "Content-Length: " + contentLength + " ",
                "").getBytes());
    }

    private void addBody(OutputStream outputStream) throws IOException {
        if (contentLength != 0) {
            outputStream.write("\r\n".getBytes());
            outputStream.write(body);
        }
    }

    public static class Builder {

        private static final HttpVersion HTTP_VERSION_DEFAULT = HttpVersion.V1_1;
        private static final HttpStatusCode HTTP_STATUS_CODE_DEFAULT = HttpStatusCode.OK;


        private HttpVersion httpVersion = HTTP_VERSION_DEFAULT;
        private HttpStatusCode httpStatusCode = HTTP_STATUS_CODE_DEFAULT;
        private String contentType;
        private String charset = "utf-8";
        private byte[] body = {};

        public Builder setHttpVersion(HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder setHttpStatusCode(HttpStatusCode httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body.getBytes();
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpVersion, httpStatusCode, contentType, charset, body);
        }
    }
}
