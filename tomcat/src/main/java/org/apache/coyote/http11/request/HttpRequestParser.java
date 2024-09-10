package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequestParser parser = new HttpRequestParser();

        String requestLine = reader.readLine();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                parser.headers.put("Content-Length", line.split(":")[1].trim());
            } else if (line.startsWith("Cookie:")) {
                parser.headers.put("Cookie", line.split(":")[1].trim());
            } else {
                String[] headerParts = line.split(":", 2);
                if (headerParts.length == 2) {
                    parser.headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }
        }

        String path = parser.readPath(requestLine);
        String method = parser.readMethod(requestLine);

        Map<String, List<String>> queryParams = parser.parsingQueryString(path);
        String fileType = parser.readFileType(path);

        if ("POST".equalsIgnoreCase(method)) {
            parser.readBody(reader);
        }

        Map<String, List<String>> body = parser.parsingBody(parser.body);

        return new HttpRequest(method, path, parser.parsingCookie(parser.headers.get("Cookie")), queryParams, fileType, body);
    }

    private String readPath(String requestLine) {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }

    private String readMethod(String requestLine) {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return null;
    }

    private Map<String, String> parsingCookie(String cookieHeader) {
        Map<String, String> cookieMap = new HashMap<>();

        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";\\s*");
            for (String cookie : cookies) {
                String[] keyValue = cookie.split("=", 2);
                if (keyValue.length == 2) {
                    cookieMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return cookieMap;
    }

    private Map<String, List<String>> parsingQueryString(String path) {
        Map<String, List<String>> queryParams = new HashMap<>();

        int questionMarkIndex = path.indexOf('?');
        String queryString = (questionMarkIndex != -1) ? path.substring(questionMarkIndex + 1) : "";

        if (queryString.isEmpty()) {
            return Collections.emptyMap();
        }

        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                queryParams.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return queryParams;
    }

    private String readFileType(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }

    private void readBody(BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        char[] bodyChars = new char[contentLength];
        reader.read(bodyChars, 0, contentLength);
        this.body = new String(bodyChars);
    }

    private Map<String, List<String>> parsingBody(String body) {
        Map<String, List<String>> bodyParams = new HashMap<>();

        if (body == null || body.isEmpty()) {
            return Collections.emptyMap();
        }

        String[] pairs = body.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                bodyParams.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return bodyParams;
    }
}




