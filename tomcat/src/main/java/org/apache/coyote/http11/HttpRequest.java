package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// 요청을 읽고 파싱해주는 클래스
public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie httpCookies;
    private final Map<String, String> parameters = new HashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        // 1. Request Line 파싱
        final String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request Line이 비어있습니다.");
        }
        log.info("request line: {}", requestLine);

        final String[] parts = requestLine.split(" ");
        this.method = parts[0];  // GET, POST

        final String uri = parts[1];
        final String queryString;
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            this.path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        } else {
            this.path = uri;
            queryString = "";
        }

        // Header & Cookie 파싱
        String line;
        String rawCookie = "";
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !"".equals(line)) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if(line.startsWith("Cookie: ")) {
                rawCookie = line.substring(8);  // "Cookie: " 이후 문자열
            }
        }
        this.httpCookies = new HttpCookie(rawCookie);

        // 3. Body 파싱 (POST 방식 데이터)
        // Content-Length는 바이트 수이므로, read()가 한 번에 다 채워준다고 가정하지 않고
        // 실제 읽은 만큼(totalRead)만 문자열로 변환한다. (한글 등 멀티바이트 문자, 패킷 분할 대응)
        String requestBody = "";
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = reader.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    break;
                }
                totalRead += read;
            }
            requestBody = new String(buffer, 0, totalRead);
        }
        parseParameters(queryString);
        parseParameters(requestBody);
    }

    private void parseParameters(String data) {
        if (data == null || data.isBlank()) {
            return;
        }
        for (String param : data.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpCookie getHttpCookies() {
        return httpCookies;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getPath() {
        return path;
    }
}
