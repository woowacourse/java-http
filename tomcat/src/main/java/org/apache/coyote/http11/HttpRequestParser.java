package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(String rawHttpRequest) {

        String[] lines = rawHttpRequest.split("\r\n");
        String[] requestLineParts = lines[0].split(" ");
        Map<String, String> headers = new HashMap<>();

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] headerParts = lines[i].split(": ");
            headers.put(headerParts[0], headerParts[1]);
            i++;
        }

        String body = "";
        if (i + 1 < lines.length) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (int j = i + 1; j < lines.length; j++) {
                bodyBuilder.append(lines[j]).append("\r\n");
            }
            body = bodyBuilder.toString().trim();
        }

        String url = requestLineParts[1];
        Map<String, String> queries = null;
        if (url.contains("?")) {
            queries = handleQueries(url);
            url = url.substring(0, url.indexOf("?"));
        }
        return new HttpRequest(requestLineParts[0],
                url,
                requestLineParts[2],
                headers,
                queries,
                body);
    }



    private static Map<String, String> handleQueries(String url) { // TODO refactor, test
        if (url.contains("?")) {
            Map<String, String> queries = new LinkedHashMap<>();
            String rawQueries = url.substring(url.indexOf("?") + 1);
            for (String qu : rawQueries.split("&")) {
                String[] split = qu.split("=");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Bad Query Parameter Format = " + qu);
                }
                queries.put(split[0], split[1]);
            }
            return queries;
        }
        return null;
    }
}