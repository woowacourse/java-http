package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.apache.coyote.http11.exception.util.ErrorResourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

    private OutputStream outputStream;
    private String version = "HTTP/1.1";
    private int code = 200;
    private String message = "OK";
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public Http11Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

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

    public void readFileFromClasspath(String resourcePath) {
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            log.error("resource not found: {}", resourcePath);
        }
        final StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", resourcePath, e);
        }

        this.body = fileContents.toString();
        headers.put("Content-Type", MediaType.detectMimeType(resourcePath));
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void sendRedirect(String path) {
        putStatusLine("HTTP/1.1 302 Found");
        putHeader("Location", path);
    }

    public void sendError(int code) throws Http11ParseException {
        Http11Status status = Http11Status.findByCode(code);
        String statusLine = status.getStatusLine();
        String[] statusLineParts = statusLine.split(" ", 3);
        this.version = statusLineParts[1];
        this.code = Integer.parseInt(statusLineParts[2]);
        this.message = statusLineParts[3];

        readFileFromClasspath(ErrorResourceMapper.getResource(400));
    }

    public void sendError(Http11Status status) throws Http11ParseException {
        sendError(status.getCode());
    }

    public void putStatusLine(String line) {
        String[] lineParts = line.split(" ", 3);
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
