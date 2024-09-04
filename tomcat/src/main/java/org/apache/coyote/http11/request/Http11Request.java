package org.apache.coyote.http11.request;

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

    public Http11Request(Http11RequestMethod method, Http11RequestHeaders header, String uri, Map<String, String> queryParameters) {
        this.method = method;
        this.header = header;
        this.uri = uri;
        this.queryParameters = queryParameters;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstLine = Http11RequestParser.parseFirstLine(br.readLine());
        assert protocol.equals(firstLine[2]);
        StringBuilder sb = new StringBuilder();
        String line;
        while (!Objects.equals(line = br.readLine(), "")) {
            sb.append(line).append(System.lineSeparator());
        }

        return new Http11Request(
                Http11RequestMethod.from(firstLine[0]),
                Http11RequestHeaders.from(sb.toString()),
                firstLine[1].split("\\?")[0],
                Http11RequestParser.parseQuery(firstLine[1]));
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

    public Http11RequestMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParameters() {
        return Map.copyOf(queryParameters);
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

        static List<Http11Accept> parseAccept(String accept) {
            if (!accept.startsWith("Accept :")) {
                return Collections.emptyList();
            }
            List<Http11Accept> list = new ArrayList<>();

            String mediaRange = accept.split(";")[0];
            for (String media : mediaRange.split(",")) {
                Http11Accept http11Accept = Http11Accept.from(media);
                if (http11Accept != null) list.add(http11Accept);
            }

            return list;
        }
    }
}
