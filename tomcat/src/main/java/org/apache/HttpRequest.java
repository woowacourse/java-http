package org.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {

    private final Map<String, String> headers;
    private final Map<String, String> payload;
    private final String method;
    private final String uri;
    private final String httpVersion;

    private HttpRequest(Map<String, String> headers, Map<String, String> payload, String method, String uri, String httpVersion) {
        this.headers = headers;
        this.payload = payload;
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> headers = new HashMap<>();


        String firstLine = reader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(firstLine);
        String method = tokenizer.nextToken();
        String uri = tokenizer.nextToken();
        String httpVersion = tokenizer.nextToken();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] strings = line.split(": ");
            String key = strings[0];
            String value = strings[1].trim();
            headers.put(key, value);
        }

        Map<String, String> payload = new HashMap<>();
        if (method.equals("POST")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] body = new char[contentLength];
            if (contentLength > 0) {
                reader.read(body, 0, contentLength);
            }

            String requestBody = new String(body);
            String decodedBody = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
            payload = parseQueryString(decodedBody);
        }

        return new HttpRequest(
                headers,
                payload,
                method,
                uri,
                httpVersion
        );
    }

    private static Map<String, String> parseQueryString(String body) {
        Map<String, String> parameters = new HashMap<>();
        String[] pairs = body.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);

            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";

            parameters.put(key, value);
        }

        return parameters;
    }

    public String getSessionIdFromCookie() {
        String[] cookies = headers.get("Cookie").split("; ");
        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID=")) {
                return cookie.substring("JSESSIONID=".length());
            }
        }
        return null;
    }

    public boolean isHttp11VersionRequest() {
        return httpVersion.equals("HTTP/1.1");
    }

    public Map<String, String> getPayload() {
        if (method.equals("GET")) {
            throw new IllegalArgumentException("GET method don't have payload");
        }
        return payload;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
