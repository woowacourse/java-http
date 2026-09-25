package org.apache.coyote.http11.model.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

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

    public static Http11Response ok(byte[] body, String contentType) {
        var headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));
        return new Http11Response(200, "OK", new ResponseHeader(headers), body);
    }

    public static Http11Response redirect(String path) {
        var headers = new LinkedHashMap<String, String>();
        headers.put("Location", path);
        headers.put("Content-Length", "0");
        return new Http11Response(302, "FOUND", new ResponseHeader(headers), new byte[0]);
    }

    public void addHeader(String name, String value) {
        header = header.with(name, value);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(header.buildHeaderResponse().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
