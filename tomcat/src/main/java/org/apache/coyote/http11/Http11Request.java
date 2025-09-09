package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Http11Request {

    private String method;
    private String uri;
    private String version;
    private Map<String, String> headers;
    private String body;
    private Http11Cookie cookie;

    public Http11Request(final InputStream inputStream) throws IOException, Http11ParseException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = reader.readLine();
        String[] requestLineParts = requestLine.split(" ");

        if (requestLineParts.length != 3) {
            throw new Http11ParseException(ParseError.INVALID_REQUEST_LINE);
        }

        this.method = requestLineParts[0];
        this.uri = requestLineParts[1];
        this.version = requestLineParts[2];

        Map<String, String> map = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        this.headers = map;

        if (map.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(map.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            this.body = new String(buffer);
        } else {
            this.body = "";
        }

        this.cookie = new Http11Cookie(map.getOrDefault("Cookie", null));
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Http11Cookie getCookie() {
        return cookie;
    }
}
