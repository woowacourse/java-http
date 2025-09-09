package org.apache.coyote.http11;

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
    private final Map<String, String> body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestStartLine = bufferedReader.readLine();
        String url = requestStartLine.split(" ")[1];
        String[] splitUrl = url.split("\\?");

        this.method = HttpMethod.of(requestStartLine.split(" ")[0]);
        this.path = splitUrl[0];
        this.queryParams = new HashMap<>();
        if (splitUrl.length == 2) {
            this.queryParams.putAll(parseQueryString(splitUrl[1]));
        }
        this.headers = readHeaders(bufferedReader);
        if (headers.containsKey("Content-Length")) {
            this.body = readBody(bufferedReader, Integer.parseInt(headers.get("Content-Length")));
        } else {
            this.body = null;
        }
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
        return parseQueryString(new String(body));
    }

    private Map<String, String> parseQueryString(String queryString) {
        return Arrays.stream(queryString.split("&")).map(s -> s.split("=", 2))
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
}
