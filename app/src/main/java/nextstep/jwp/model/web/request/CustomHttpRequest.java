package nextstep.jwp.model.web.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomHttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    public CustomHttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> params) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.params = params;
    }

    public static CustomHttpRequest from(BufferedReader reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new LinkedHashMap<>();

        RequestLine requestLine = new RequestLine(reader.readLine().split(" "));
        String method = requestLine.getMethod();
        String uri = requestLine.getPath();
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

        return new CustomHttpRequest(requestLine, headers, params);
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
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersionOfProtocol() {
        return requestLine.getVersionOfProtocol();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
