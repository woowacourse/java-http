package org.apache.coyote.http11;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headersMap;

    private HttpRequestHeader(Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }

    public static HttpRequestHeader from(String headerContent) {
        Map<String, String> headersMap = new HashMap<>();
        List<String> lines = Arrays.stream(headerContent.split(System.lineSeparator())).collect(toList());
        parseRequestLine(headersMap, lines);
        parseRequestHeaders(headersMap, lines);
        return new HttpRequestHeader(headersMap);
    }

    private static void parseRequestHeaders(Map<String, String> headersMap, List<String> lines) {
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int standardIndex = line.indexOf(":");
            String header = line.substring(0, standardIndex).strip();
            String content = line.substring(standardIndex + 1).strip();
            headersMap.put(header, content);
        }
    }

    private static void parseRequestLine(Map<String, String> headersMap, List<String> lines) {
        List<String> httpInformation = Arrays.stream(lines.get(0).split(" ")).collect(toList());
        headersMap.put("HTTP Method", httpInformation.get(0));
        headersMap.put("URL", httpInformation.get(1));
        headersMap.put("HTTP version", httpInformation.get(2));
    }

    public String httpMethod() {
        return headersMap.get("HTTP Method");
    }

    public String url() {
        return headersMap.get("URL");
    }

    public String contentLength() {
        return headersMap.get("Content-Length");
    }

    public Map<String, String> headersMap() {
        return new HashMap<>(headersMap);
    }
}
