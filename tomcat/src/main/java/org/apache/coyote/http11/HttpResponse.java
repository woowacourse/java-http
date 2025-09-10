package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, byte[] body) {
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

}
