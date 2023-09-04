package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestMessage {

    private final BufferedReader reader;
    private final Map<String, String> headerMap;

    public RequestMessage(BufferedReader reader, Map<String, String> headerMap) {
        this.reader = reader;
        this.headerMap = headerMap;
    }

    public static RequestMessage from(BufferedReader reader) throws IOException {
        final Map<String, String> map = new HashMap<>();
        String line = "";
        while (true) {
            line = reader.readLine();
            if ("".equals(line)) {
                return new RequestMessage(reader, map);
            }
            getKeyAndValue(line, map, ": ");
        }
    }

    private static void getKeyAndValue(String line, Map<String, String> map, String regex) {
        Arrays.stream(line.split("&"))
                .forEach(splitQuery -> map.put(findKey(splitQuery, regex), findValue(splitQuery, regex)));
    }

    private static String findKey(String line, String regex) {
        String key = line.substring(0, line.indexOf(regex));
        return key.strip();
    }

    private static String findValue(String line, String regex) {
        String value = line.substring(line.indexOf(regex) + 1);
        return value.strip();
    }

    public Map<String, String> getRequestBody() throws IOException {
        if(Objects.isNull(headerMap.get("Content-Length"))){
            return new HashMap<>();
        }
        int contentLength = Integer.parseInt(headerMap.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        if(hasQuery(requestBody)){
            return getMap(requestBody);
        }
        return new HashMap<>();
    }

    private static boolean hasQuery(String line) {
        return line.contains("?") || line.contains("=");
    }

    private static Map<String, String> getMap(String line) {
        Map<String, String> map = new HashMap<>();
        getKeyAndValue(line, map, "=");
        return map;
    }
}
