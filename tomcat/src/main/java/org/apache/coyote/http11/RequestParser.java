package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);

    private final BufferedReader bufferedReader;
    private final String[] requestLine;
    private final Map<String, String> header;
    private final String body;

    public RequestParser(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        String line = bufferedReader.readLine();
        log.info("requestLine : {}", line);
        this.requestLine = line.split(" ");
        this.header = extractHeader();
        this.body = extractBody();
    }

    private Map<String, String> extractHeader() throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] keyValue = line.split(":");
            header.put(keyValue[0], keyValue[1].trim());
            line = bufferedReader.readLine();
        }
        return Collections.unmodifiableMap(header);
    }

    private String extractBody() throws IOException {
        if (header.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public String getMethod() {
        return requestLine[0];
    }

    public String getRequestUri() {
        return requestLine[1];
    }

    public String getBody() {
        return body;
    }
}
