package org.apache.coyote.util.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.util.Cookie;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLineString = br.readLine();
        if (requestLineString == null || requestLineString.isEmpty()) {
            return null;
        }

        RequestLine requestLine = new RequestLine(requestLineString);
        Map<String, String> headers = parseHeaders(br);
        Map<String, String> body = parseBody(br, headers);
        Cookie cookie = Cookie.parse(headers.get("cookie"));

        return new HttpRequest(requestLine, headers, body, cookie);
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

    private static Map<String, String> parseBody(BufferedReader br, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        if (contentLength == 0) {
            return new HashMap<>();
        }
        char[] bodyChars = new char[contentLength];
        br.read(bodyChars, 0, contentLength);
        String bodyString = new String(bodyChars);
        return parseQueryString(bodyString);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return queryParams;
        }
        String[] pairs = queryString.split("&");
        return Arrays.stream(pairs)
                .map(pair -> pair.split("=", 2))
                .collect(HashMap::new, (map, pair) -> {
                    String key = urlDecode(pair[0]);
                    String value = pair.length > 1 ? urlDecode(pair[1]) : "";
                    map.put(key, value);
                }, HashMap::putAll);
    }

    private static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
