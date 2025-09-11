package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.response.header.ContentLength;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.ResponseHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record HttpResponse(
        HttpVersion version,
        HttpStatus status,
        ResponseBody responseBody,
        List<ResponseHeader> headers
) {

    private static final String CRLF = "\r\n";

    public static Builder http11Builder(HttpStatus status) {
        return new Builder(HttpVersion.HTTP1_1, status);
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write((version.getName() + " " + status.getPhrase() + CRLF).getBytes(StandardCharsets.UTF_8));

        for (ResponseHeader header : headers) {
            outputStream.write((header.getName() + ": " + header.getValue() + CRLF).getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));

        if (responseBody != null) {
            outputStream.write(responseBody.data());
        }
    }

    public static class Builder {
        private final HttpVersion version;
        private final HttpStatus status;
        private final List<ResponseHeader> headers = new ArrayList<>();
        private ResponseBody responseBody = null;

        public Builder(HttpVersion version, HttpStatus status) {
            this.version = version;
            this.status = status;
        }

        public static Builder http11(HttpStatus status) {
            return new Builder(HttpVersion.HTTP1_1, status);
        }

        public Builder body(ResponseBody responseBody) {
            this.responseBody = responseBody;
            headers.add(ContentType.fromResponseBody(responseBody));
            headers.add(ContentLength.fromResponseBody(responseBody));
            return this;
        }

        public Builder header(ResponseHeader header) {
            this.headers.add(header);
            return this;
        }

        public Builder headers(Collection<ResponseHeader> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(version, status, responseBody, headers);
        }
    }
}
