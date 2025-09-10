package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.exception.Http11ParseException;

public class Http11Response {

    private static final String CRLF = "\r\n";

    private String version;
    private int code;
    private String message;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public byte[] buildResponse(Charset charset) throws IOException {
        final String statusLine = String.join(" ", version, String.valueOf(code), message);
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine).append(CRLF);
        for (Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }
        responseBuilder.append(CRLF);
        responseBuilder.append(body);
        return responseBuilder.toString().getBytes(charset);
    }

    public void putStatusLine(String line) throws Http11ParseException {
        String[] lineParts = line.split(" ");
        this.version = lineParts[0];
        this.code = Integer.parseInt(lineParts[1]);
        this.message = lineParts[2];
    }

    public void putHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void putHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void putBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
