package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final Map<String, String> body;
    private final Session session;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestStartLine = bufferedReader.readLine();
        String url = requestStartLine.split(" ")[1];
        String[] splitUrl = url.split("\\?");

        this.method = HttpMethod.of(requestStartLine.split(" ")[0]);
        this.path = splitUrl[0];
        this.queryParams = new HashMap<>();
        if (splitUrl.length == 2) {
            this.queryParams.putAll(parseKeyValuePairs(splitUrl[1], "&"));
        }
        this.headers = readHeaders(bufferedReader);
        this.cookies = readCookies(headers);
        this.session = SessionManager.getInstance().findSession((getCookie("JSESSIONID")));
        if (headers.containsKey("Content-Length")) {
            this.body = readBody(bufferedReader, Integer.parseInt(headers.get("Content-Length")));
        } else {
            this.body = null;
        }
    }

    private Map<String, String> readCookies(Map<String, String> headers) {
        if (!headers.containsKey("Cookie")) {
            return Map.of();
        }
        String value = headers.get("Cookie");
        return parseKeyValuePairs(value, "; ");
    }

    private Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] keyAndValue = line.split(": ");
            headers.put(keyAndValue[0], keyAndValue[1]);
        }
        return headers;
    }

    private Map<String, String> readBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return parseKeyValuePairs(new String(body), "&");
    }

    private Map<String, String> parseKeyValuePairs(String input, String pairDelimiter) {
        return Arrays.stream(input.split(pairDelimiter)).map(s -> s.split("=", 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    public boolean equalPath(String path) {
        return this.path.equals(path);
    }

    public boolean equalMethod(HttpMethod method) {
        return this.method == method;
    }

    public boolean isStaticResourcePath() {
        return this.path.matches(".+\\..+");
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String key) {
        return queryParams.get(key);
    }

    public String getBody(String key) {
        return body.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public Session getSession() {
        return session;
    }
}
