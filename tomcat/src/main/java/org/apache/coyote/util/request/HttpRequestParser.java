package org.apache.coyote.util.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.util.Cookie;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String[] requestLineParts = readRequestLine(br);
        if (requestLineParts == null) {
            return null;
        }

        String method = requestLineParts[0];
        String path = parsePath(requestLineParts[1]);
        Map<String, String> queries = parseUrlQueries(requestLineParts[1]);
        String version = requestLineParts[2];

        Map<String, String> headers = parseHeaders(br);
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        String cookieHeader = headers.get("cookie");

        if ("POST".equalsIgnoreCase(method) && contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            String body = new String(bodyChars);
            queries.putAll(parseBodyQueries(body));
        }

        Cookie cookie = Cookie.parse(cookieHeader);
        return new HttpRequest(method, path, version, queries, cookie);
    }

    private static String[] readRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null || line.isEmpty()) {
            return null;
        }
        String[] firstLine = line.split(" ", 3);
        if (firstLine.length != 3) {
            return null;
        }
        return firstLine;
    }

    private static Map<String, String> parseHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                String key = headerParts[0].trim().toLowerCase();
                String value = headerParts[1].trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static String parsePath(String fullPath) {
        int queryStart = fullPath.indexOf('?');
        if (queryStart != -1) {
            return fullPath.substring(0, queryStart);
        }
        return fullPath;
    }

    private static Map<String, String> parseUrlQueries(String fullPath) {
        int queryStart = fullPath.indexOf('?');
        if (queryStart != -1 && queryStart < fullPath.length() - 1) {
            return parseQueryString(fullPath.substring(queryStart + 1));
        }
        return new HashMap<>();
    }

    private static Map<String, String> parseBodyQueries(String query) {
        return parseQueryString(query);
    }

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> queryMap = new HashMap<>();
        if (query == null || query.isBlank()) {
            return queryMap;
        }
        for (String pair : query.split("&")) {
            int eqIdx = pair.indexOf("=");
            String key, value;
            if (eqIdx > -1) {
                key = URLDecoder.decode(pair.substring(0, eqIdx), StandardCharsets.UTF_8);
                value = URLDecoder.decode(pair.substring(eqIdx + 1), StandardCharsets.UTF_8);
            } else {
                key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                value = "";
            }
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
