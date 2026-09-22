package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    private final String httpVersion;
    private int statusCode;
    private String reasonPhrase;
    private HttpHeaders header;
    private byte[] body;

    public HttpResponse() {
        this("HTTP/1.1", 200, "OK", new HttpHeaders(Map.of()), new byte[0]);
    }

    public HttpResponse(
            String httpVersion,
            int statusCode,
            String reasonPhrase,
            HttpHeaders header,
            byte[] body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.header = header;
        this.body = body;
    }

    public void ok(String contentType, String content) {
        Map<String, String> headers = new HashMap<>(header.getValues());
        headers.remove("location");
        headers.remove("content-length");
        headers.put("content-type", contentType);

        this.statusCode = 200;
        this.reasonPhrase = "OK";
        this.header = new HttpHeaders(headers);
        this.body = content.getBytes(StandardCharsets.UTF_8);
    }

    public void redirect(String location) {
        Map<String, String> headers = new HashMap<>(header.getValues());
        headers.remove("content-type");
        headers.remove("content-length");
        headers.put("location", location);

        this.statusCode = 302;
        this.reasonPhrase = "Found";
        this.header = new HttpHeaders(headers);
        this.body = new byte[0];
    }

    public String getStatusLine() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }

    public int getContentLength() {
        return body.length;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }
}
