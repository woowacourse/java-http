package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    private OutputStream outputStream;

    private String responseStatus;

    private String contentType;

    private String charSet;

    private int contentLength;

    private String responseBody;

    private HttpResponse(OutputStream outputStream, String responseStatus, String contentType, String charSet, String responseBody) {
        this.outputStream = outputStream;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.charSet = charSet;
        this.responseBody = responseBody;
        this.contentLength = this.responseBody.getBytes().length;
    }

    public void flush() throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 " + this.responseStatus,
                "Content-Type: " + this.contentType + ";charset=" + this.charSet,
                "Content-Length: " + this.contentLength + " ",
                "",
                this.responseBody
        );

        outputStream.write(response.getBytes());
        outputStream.flush();
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
}
