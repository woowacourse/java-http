package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomHttpRequest {
    private final String method;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    public CustomHttpRequest(String method, String path, String httpVersion,
                             Map<String, String> headers, Map<String, String> params) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.params = params;
    }

    public static CustomHttpRequest from(BufferedReader reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new LinkedHashMap<>();

        String[] requestInfo = reader.readLine().split(" ");
        String method = requestInfo[0];
        String uri = requestInfo[1];
        String httpVersion = requestInfo[2];
        String path = uri;

        String line = "";
        while (!("".equals(line = reader.readLine()))) {
            String[] keyAndValue = line.split(": ");
            headers.put(keyAndValue[0].trim(), keyAndValue[1].trim());
        }

        if (method.equals("GET")) {
            path = dividePathFromUri(uri);
            if (uri.contains("?")) {
                parseParams(params, uri.substring(path.length() + 1));
            }
        }

        if (method.equals("POST")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            requestBody = URLDecoder.decode(requestBody, "UTF-8");
            parseParams(params, requestBody);
        }

        return new CustomHttpRequest(method, path, httpVersion, headers, params);
    }

    private static String dividePathFromUri(String uri) {
        if (!uri.contains("?")) {
            uri = appendSuffixIfNotAppended(uri);
            return uri;
        }
        return uri.substring(0, uri.indexOf('?'));
    }

    private static String appendSuffixIfNotAppended(String uri) {
        if (uri.contains(".")) {
            return uri;
        }
        return uri + ".html";
    }

    private static void parseParams(Map<String, String> params, String rawParams) {
        String[] paramPairs = rawParams.split("&");

        for (String paramPair : paramPairs) {
            int delimiterIdx = paramPair.indexOf("=");
            String key = paramPair.substring(0, delimiterIdx);
            String value = paramPair.substring(delimiterIdx + 1);
            params.put(key, value);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
