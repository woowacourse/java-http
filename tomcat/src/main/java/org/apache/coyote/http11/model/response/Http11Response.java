package org.apache.coyote.http11.model.response;

import java.io.IOException;
import java.io.OutputStream;

public class Http11Response {
    private int statusCode;
    private String statusMessage;
    private ResponseHeader header;
    private byte[] body;

    public Http11Response(int statusCode, String statusMessage, ResponseHeader header, byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.header = header;
        this.body = body;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(header.buildHeaderResponse().getBytes());
        outputStream.write(body);
    }
}
