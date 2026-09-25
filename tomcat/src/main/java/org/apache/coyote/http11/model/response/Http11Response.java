package org.apache.coyote.http11.model.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Http11Response {
    private int statusCode;
    private String statusMessage;
    private ResponseHeader header;
    private byte[] body;

    private Http11Response(int statusCode, String statusMessage, ResponseHeader header, byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.header = header;
        this.body = body;
    }

    public static Http11Response empty() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Length", "0");
        return new Http11Response(200, "OK", new ResponseHeader(headers), new byte[0]);
    }

    public void ok(String contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));
        setStatusCode(200);
        setStatusMessage("OK");
        setHeader(new ResponseHeader(headers));
    }

    public void redirect(String path) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", path);
        headers.put("Content-Length", "0");
        setStatusCode(302);
        setStatusMessage("FOUND");
        setBody(new byte[0]);
        setHeader(new ResponseHeader(headers));
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

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
