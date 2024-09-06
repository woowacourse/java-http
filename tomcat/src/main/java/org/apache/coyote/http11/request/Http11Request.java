package org.apache.coyote.http11.request;

import org.apache.catalina.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Http11Request {

    private static final String protocol = "HTTP/1.1";
    private static final List<String> extensions = List.of(".html", ".css", ".js", ".ico");

    private final Http11RequestMethod method;
    private final Http11RequestHeaders header;
    private final String uri;
    private final Map<String, String> queryParameters;
    private final Http11RequestBody requestBody;

    public Http11Request(Http11RequestMethod method, Http11RequestHeaders header, String uri, Map<String, String> queryParameters, Http11RequestBody requestBody) {
        this.method = method;
        this.header = header;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.requestBody = requestBody;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstLine = Http11RequestParser.parseFirstLine(br.readLine());
        assert protocol.equals(firstLine[2]);
        StringBuilder headerSb = new StringBuilder();
        String headerLine;
        while (!Objects.equals(headerLine = br.readLine(), "")) {
            headerSb.append(headerLine).append(System.lineSeparator());
        }
        Http11RequestHeaders http11RequestHeaders = Http11RequestHeaders.from(headerSb.toString());

        Http11RequestBody body = null;
        if (http11RequestHeaders.contains("Content-Length")) {
            int contentLength = Integer.parseInt(http11RequestHeaders.get("Content-Length").trim());
            char[] buffer = new char[contentLength];
            br.read(buffer, 0, contentLength);
            body = Http11RequestBody.from(new String(buffer));
        }

        return new Http11Request(
                Http11RequestMethod.from(firstLine[0]),
                http11RequestHeaders,
                firstLine[1], //.split("\\?")[0],
                Http11RequestParser.parseQuery(firstLine[1]),
                body);
    }

    public boolean isStaticRequest() {
        return method == Http11RequestMethod.GET && isFile();
    }

    private boolean isFile() {
        for (String extension : extensions) {
            if (uri.endsWith(extension)) return true;
        }
        return false;
    }

    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        for (String cookieString : header.get("Cookie").split(";")) {
            String[] keyValue = cookieString.split("=");
            assert keyValue.length == 2;
            cookies.add(new Cookie(keyValue[0], keyValue[1]));
        }
        return cookies;
    }

    public Http11RequestMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParameters() {
        return Map.copyOf(queryParameters);
    }

    public Http11RequestBody getRequestBody() {
        return requestBody;
    }

    public Http11RequestHeaders getHeader() {
        return header;
    }

    private static class Http11RequestParser {

        static String[] parseFirstLine(String firstLine) {
            String[] split = firstLine.split(" ");
            assert split.length == 3;
            return split;
        }

        static Map<String, String> parseQuery(String url) {
            if (!url.contains("?")) return Collections.emptyMap();
            Map<String, String> queryMap = new HashMap<>();

            String queries = url.split("\\?")[1];
            for (String query : queries.split("&")) {
                String[] keyValue = query.split("=");
                assert keyValue.length == 2;
                queryMap.put(keyValue[0], keyValue[1]);
            }
            return queryMap;
        }
    }

    @Override
    public String toString() {
        return "Http11Request{" +
                "method=" + method +
                ", header=" + header +
                ", uri='" + uri + '\'' +
                ", queryParameters=" + queryParameters +
                ", requestBody=" + requestBody +
                '}';
    }
}
