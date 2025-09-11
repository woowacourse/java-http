package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(HttpStatus httpStatus, Map<String, String> headers, byte[] body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("HTTP/1.1 %s %s \r\n".formatted(httpStatus.getCode(), httpStatus.getMessage()).getBytes());
        if (headers != null) {
            baos.write(createHeaderBytes());
        }
        if (body != null) {
            baos.write(body);
        }
        return baos.toByteArray();
    }

    private byte[] createHeaderBytes() {
        StringBuilder headerBuilder = new StringBuilder();
        headers.forEach((key, value) ->
                headerBuilder.append(key)
                        .append(": ")
                        .append(value)
                        .append("\r\n")
        );
        headerBuilder.append("\r\n");
        return headerBuilder.toString().getBytes();
    }

    public static Builder ok() {
        return new Builder(HttpStatus.OK);
    }

    public static Builder found() {
        return new Builder(HttpStatus.FOUND);
    }

    public static Builder badRequest() {
        return new Builder(HttpStatus.BAD_REQUEST);
    }

    public static Builder notFound() {
        return new Builder(HttpStatus.NOT_FOUND);
    }

    public static Builder methodNotAllowed() {
        return new Builder(HttpStatus.METHOD_NOT_ALLOWED);
    }

    public static Builder internalServerError() {
        return new Builder(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class Builder {
        private HttpStatus httpStatus;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body;

        public Builder(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.httpStatus, this.headers, this.body);
        }
    }
}
