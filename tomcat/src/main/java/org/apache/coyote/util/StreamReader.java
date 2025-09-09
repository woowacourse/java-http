package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.constant.RequestLine;

public class StreamReader {

    public static HttpRequest readRequest(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final RequestLine requestLine = readRequestLine(reader);
        if (requestLine == null) {
            return null;
        }
        final Map<String, String> headers = readHeaders(reader);
        final String body = readBodyIfPresent(reader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static RequestLine readRequestLine(BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        return RequestLine.from(line);
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            parseHeaderLine(line, headers);
        }
        return headers;
    }
    
    private static void parseHeaderLine(String line, Map<String, String> headers) {
        final String[] parts = line.split(":", 2);
        if (parts.length == 2) {
            headers.put(parts[0].trim(), parts[1].trim());
        }
    }
    
    private static String readBodyIfPresent(BufferedReader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return null;
        }
        final int contentLength = parseContentLength(headers.get("Content-Length"));
        return contentLength > 0 ? readBody(reader, contentLength) : null;
    }
    
    private static int parseContentLength(String contentLengthValue) {
        try {
            return Integer.parseInt(contentLengthValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        
        while (totalRead < contentLength) {
            final int charsRead = reader.read(buffer, totalRead, contentLength - totalRead);
            if (charsRead == -1) {
                break;
            }
            totalRead += charsRead;
        }
        
        return totalRead > 0 ? new String(buffer, 0, totalRead) : null;
    }
}
