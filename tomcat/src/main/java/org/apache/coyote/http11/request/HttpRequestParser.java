package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        List<String> header = readHeaders(reader);

        return readHttpRequest(reader, requestLine, header);
    }

    private static HttpRequest readHttpRequest(BufferedReader reader, String requestLine, List<String> header)
            throws IOException {
        HttpRequestHeader httpRequestHeader = readHttpRequestHeader(requestLine, header);

        String method = readMethod(requestLine);
        String path = readPath(requestLine);
        String contentLength = extractHeaderValue("Content-Length", header);
        String body = null;
        if ("POST".equalsIgnoreCase(method)) {
            body = readBody(reader, contentLength);
        }

        return new HttpRequest(method, path, httpRequestHeader, parsingBody(body));
    }

    private static HttpRequestHeader readHttpRequestHeader(String requestLine, List<String> header) {
        String cookieHeader = extractHeaderValue("Cookie", header);
        String path = readPath(requestLine);
        Map<String, List<String>> queryParams = parsingQueryString(path);
        String fileType = readFileType(path);
        return new HttpRequestHeader(parsingCookie(cookieHeader), queryParams, fileType);
    }

    private static List<String> readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private static String extractHeaderValue(String headerName, List<String> headerLines) {
        for (String line : headerLines) {
            if (line.startsWith(headerName + ":")) {
                return line.split(":", 2)[1].trim();
            }
        }
        return null;
    }

    private static String readMethod(String requestLine) {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            return parts.length > 0 ? parts[0] : null;
        }
        return null;
    }

    private static String readPath(String requestLine) {
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            return parts.length >= 2 ? parts[1] : null;
        }
        return null;
    }

    private static Map<String, List<String>> parsingQueryString(String path) {
        Map<String, List<String>> queryParams = new HashMap<>();
        int questionMarkIndex = path.indexOf('?');
        String queryString = (questionMarkIndex != -1) ? path.substring(questionMarkIndex + 1) : "";

        if (!queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    queryParams.computeIfAbsent(keyValue[0], k -> new ArrayList<>()).add(keyValue[1]);
                }
            }
        }
        return queryParams;
    }

    private static String readFileType(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }

    private static String readBody(BufferedReader reader, String contentLengthStr) throws IOException {
        if (contentLengthStr == null) {
            return null;
        }
        int contentLength = Integer.parseInt(contentLengthStr);
        char[] bodyChars = new char[contentLength];
        reader.read(bodyChars, 0, contentLength);
        return new String(bodyChars);
    }

    private static Map<String, String> parsingCookie(String cookieHeader) {
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

    private static Map<String, List<String>> parsingBody(String body) {
        Map<String, List<String>> bodyParams = new HashMap<>();
        if (body != null && !body.isEmpty()) {
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    bodyParams.computeIfAbsent(keyValue[0], k -> new ArrayList<>()).add(keyValue[1]);
                }
            }
        }
        return bodyParams;
    }
}






