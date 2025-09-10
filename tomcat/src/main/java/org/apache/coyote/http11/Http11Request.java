package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.apache.coyote.http11.exception.ParseError;

public class Http11Request {

    private Http11Method method;
    private String uri;
    private String version;
    private String path;
    private Map<String, String> params;
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

        this.method = Http11Method.from(requestLineParts[0]);
        this.uri = requestLineParts[1];
        this.version = requestLineParts[2];

        int queryIndex = uri.indexOf("?");
        if (queryIndex != -1) {
            this.path = uri.substring(0, queryIndex);
        } else {
            this.path = uri; // 전체를 path 로 사용
        }

        Map<String, String> map = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        this.headers = map;

        if (map.containsKey("Content-Length")) { // TODO: Body 파싱 최적화
            int contentLength = Integer.parseInt(map.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            this.body = new String(buffer);
        } else {
            this.body = "";
        }

        if (this.body.isEmpty()) {
            this.params = Collections.emptyMap();
        } else {
            final Map<String, String> params = new HashMap<>();
            final String[] pairs = body.split("&");
            for (String pair : pairs) {
                final String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
            this.params = params;
        }

        this.cookie = new Http11Cookie(map.getOrDefault("Cookie", null));
    }

    public boolean isGet() {
        return method == Http11Method.GET;
    }

    public boolean isPost() {
        return method == Http11Method.POST;
    }

    public Http11Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
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
