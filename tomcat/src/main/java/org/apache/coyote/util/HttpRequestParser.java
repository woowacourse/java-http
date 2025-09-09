package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] httpLine = readHttpLine(br);
        if (httpLine == null) {
            return null;
        }
        String method = httpLine[0];
        String path = parsePath(httpLine[1]);
        Map<String, String> queries = parseQueries(httpLine[1]);
        String version = httpLine[2];
        return new HttpRequest(method, path, version, queries);
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
}
