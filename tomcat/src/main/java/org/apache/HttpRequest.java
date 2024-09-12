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

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpRequestBody requestBody;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> headers = new HashMap<>();


        String firstLine = reader.readLine();
        RequestLine requestLine = RequestLine.from(firstLine);

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] strings = line.split(": ");
            String key = strings[0];
            String value = strings[1].trim();
            headers.put(key, value);
        }

        HttpRequestBody body = HttpRequestBody.empty();
        if (requestLine.getMethod() == HttpMethod.POST) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] unparsedBody = new char[contentLength];
            if (contentLength > 0) {
                reader.read(unparsedBody, 0, contentLength);
            }

            String requestBody = new String(unparsedBody);
            String decodedBodyLine = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
            body = HttpRequestBody.from(decodedBodyLine);
        }

        return new HttpRequest(
            requestLine,
            headers,
            body
        );
    }

    public String getSessionIdFromCookie() {
        if (headers.get("Cookie") != null) {
            String[] cookies = headers.get("Cookie").split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID=")) {
                    return cookie.substring("JSESSIONID=".length());
                }
            }
        }
        return null;
    }

    public boolean isHttp11VersionRequest() {
        return requestLine.isHttp11Version();
    }

    public HttpRequestBody getRequestBody() {
        if (requestLine.getMethod() == HttpMethod.GET) {
            throw new IllegalArgumentException("GET method don't have payload");
        }
        return requestBody;
    }

    public boolean hasMethod(HttpMethod method) {
        return this.requestLine.getMethod() == method;
    }

    public String getUri() {
        return requestLine.getPath();
    }
}
