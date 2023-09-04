package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpRequest {

    private final Map<String, String> values = new ConcurrentHashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean isStartLine = true;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            if (isStartLine) {
                String[] startLine = line.split(" ");
                values.put("method", startLine[0]);
                values.put("target", startLine[1]);
                values.put("version", startLine[2]);
                isStartLine = false;
            } else {
                String[] messages = line.split(": ");
                values.put(messages[0], messages[1]);
            }
        }

        String contentLength = values.get("Content-Length");
        if (contentLength != null) {
            int length = Integer.parseInt(contentLength);
            char[] buffer = new char[length];
            bufferedReader.read(buffer, 0, length);
            values.put("body", new String(buffer));
        }
    }

    public String getUri() {
        String target = values.get("target");
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            return target;
        }

        return target.substring(0, queryStringIdx);
    }

    public Map<String, String> getQueryString() {
        String target = values.get("target");
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            throw new IllegalStateException();
        }

        String queryStrings = target.substring(queryStringIdx + 1);
        return parseKeyAndValue(queryStrings);
    }

    public Map<String, String> getBody() {
        return parseKeyAndValue(values.get("body"));
    }

    private Map<String, String> parseKeyAndValue(String input) {
        return Arrays.stream(input.split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
    }

    public HttpMethod getMethod() {
        return HttpMethod.from(values.get("method"));
    }

    public HttpCookie getCookie() {
        if (values.get("Cookie") == null) {
            return null;
        }
        Map<String, String> cookie = Arrays.stream(values.get("Cookie").split("; "))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
        return new HttpCookie(cookie);
    }
}
