package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> header;
    private final Map<String, String> query;

    private HttpRequest(String method, String path, Map<String, String> header, Map<String, String> query) {
        this.method = method;
        this.path = path;
        this.header = header;
        this.query = query;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = br.readLine();
        String[] parsedRequestLine = requestLine.split(" ");
        String method = parsedRequestLine[0];
        String url = parsedRequestLine[1];
        Map<String, String> header = parseHeader(br);
        Map<String, String> query = new HashMap<>();
        if (header.containsKey(CONTENT_LENGTH)) {
            query.putAll(parseQuery(parseBody(br, Integer.parseInt(header.get(CONTENT_LENGTH)))));
        }

        int queryStartIndex = url.indexOf("?");

        if (queryStartIndex != -1) {
            query.putAll(parseQuery(url.substring(queryStartIndex + 1)));
            url = url.substring(0, queryStartIndex);
        }

        return new HttpRequest(method, url, header, query);
    }

    private static Map<String, String> parseHeader(BufferedReader request) throws IOException {
        HashMap<String, String> header = new HashMap<>();
        String line = null;
        while (true) {
            line = request.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] keyAndValue = line.split(":");
            header.put(keyAndValue[0], keyAndValue[1].trim());
        }
        return header;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> queryMap = new HashMap<>();

        String[] data = query.split("&");
        for (String each : data) {
            String[] keyAndValue = each.split("=");
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return queryMap;
    }

    private static String parseBody(BufferedReader request, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        request.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean hasHeaderValue(String headerValue) {
        return header.containsKey(headerValue);
    }

    public String getHeaderValue(String headerValue) {
        return header.get(headerValue);
    }

    public boolean isMatchedPath(String path) {
        return this.path.startsWith(path);
    }

    public boolean checkMethod(String method) {
        return this.method.equals(method);
    }

    public String getQueryValue(String queryValue) {
        return query.get(queryValue);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
