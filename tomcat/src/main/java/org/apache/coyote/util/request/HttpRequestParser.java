package org.apache.coyote.util.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.util.Cookie;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] httpLine = readHttpLine(br);
        if (httpLine == null) {
            return null;
        }
        String method = httpLine[0];
        String path = parsePath(httpLine[1]);
        Map<String, String> queries = new HashMap<>();
        Map<String, String> getQueries = parseQueries(httpLine[1]);
        if (getQueries != null) {
            queries.putAll(getQueries);
        }
        String version = httpLine[2];
        int contentLength = 0;
        String cookieHeader = null;
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("Cookie:")) {
                cookieHeader = line.substring(7).trim();
            }
        }
        if ("POST".equalsIgnoreCase(method) && contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            String body = new String(bodyChars);
            Map<String, String> postQueries = parseQueryString(body);
            queries.putAll(postQueries);
        }
        Cookie cookie = Cookie.parse(cookieHeader);
        return new HttpRequest(method, path, version, queries, cookie);
    }

    private static String[] readHttpLine(BufferedReader br) throws IOException {
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

    private static String parsePath(String path) {
        String[] splitPath = path.split("[?]");
        return splitPath[0];
    }

    private static Map<String, String> parseQueries(String path) {
        String[] splitPath = path.split("[?]");
        if (splitPath.length != 2) {
            return null;
        }
        String query = splitPath[1];
        Map<String, String> queries = new HashMap<>();
        for (String pair : query.split("&")) {
            parseKeyValue(pair, queries);
        }
        return queries;
    }

    private static void parseKeyValue(String pair, Map<String, String> queries) {
        if (pair.isEmpty()) {
            return;
        }
        String key;
        String value = "";
        int separatorIdx = pair.indexOf('=');
        if (separatorIdx >= 0) {
            key = pair.substring(0, separatorIdx);
            value = pair.substring(separatorIdx + 1);
        } else {
            key = pair;
        }
        queries.put(key, value);
    }

    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> queryMap = new HashMap<>();
        if (query == null || query.isBlank()) {
            return queryMap;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
