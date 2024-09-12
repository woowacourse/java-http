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
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";

    private final String[] requestLine;
    private final Map<String, String> header;
    private final String body;
    private final HttpCookie httpCookie;

    public RequestParser(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        log.info("requestLine : {}", line);
        this.requestLine = line.split(" ");
        this.header = extractHeader(bufferedReader);
        this.httpCookie = new HttpCookie(extractCookie());
        this.body = extractBody(bufferedReader);
    }

    private Map<String, String> extractHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] keyValue = line.split(":");
            header.put(keyValue[0], keyValue[1].trim());
            line = bufferedReader.readLine();
        }
        return Collections.unmodifiableMap(header);
    }

    private String extractCookie() {
        if (header.containsKey(COOKIE)) {
            return header.get(COOKIE);
        }
        return "";
    }

    private String extractBody(BufferedReader bufferedReader) throws IOException {
        if (header.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(header.get(CONTENT_LENGTH));
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

    public String getCookie(String cookieName) {
        return httpCookie.getCookie(cookieName);
    }
}
