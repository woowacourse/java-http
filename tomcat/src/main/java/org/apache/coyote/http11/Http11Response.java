package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

    private String version;
    private int code;
    private String message;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public byte[] buildResponse(Charset charset) throws IOException {
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
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

    public String readFileFromClasspath(String resourcePath) {
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            log.error("resource not found: {}", resourcePath);
            return "";
        }
        final StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", resourcePath, e);
            return "";
        }
        return fileContents.toString();
    }

    public void sendRedirect(String path) throws Http11ParseException {
        putStatusLine("HTTP/1.1 302 Found");
        putHeader("Location", "/index.html");
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
