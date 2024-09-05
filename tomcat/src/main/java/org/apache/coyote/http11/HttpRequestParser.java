package org.apache.coyote.http11;

import java.util.HashMap;
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
            bodyBuilder.toString().trim();
        }

        return new HttpRequest(requestLineParts[0],
                parseUrl(requestLineParts[1]),
                requestLineParts[2],
                headers,
                body);
    }

    private static String parseUrl(String rawUrl) {
        if (rawUrl.endsWith(".html")) {
            return "static/" + rawUrl.substring(1);
        }
        return rawUrl.substring(1);
    }
}