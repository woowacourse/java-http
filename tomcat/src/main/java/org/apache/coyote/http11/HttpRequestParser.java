package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private StartLine startLine;
    private Map<String, String> header = new HashMap<>();
    private String messageBody;

    public void accept(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String firstLine = bufferedReader.readLine();

        startLine = new StartLine(firstLine);
        readHeader(bufferedReader);
        readMessageBody(bufferedReader);
    }

    private void readHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] split = line.split(":");
            header.put(split[KEY_INDEX], split[VALUE_INDEX].trim());
            line = bufferedReader.readLine();
        }
    }

    private void readMessageBody(BufferedReader bufferedReader) throws IOException {
        String key = "Content-Length";
        if (header.containsKey(key)) {
            int contentLength = Integer.parseInt(header.get(key));
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            messageBody = new String(body);
        }
    }

    public Map<String, String> findCookies() {
        return header.entrySet().stream()
                .filter(entry -> entry.getKey().equals("Cookie"))
                .map(entry -> entry.getValue().split("; "))
                .flatMap(Arrays::stream)
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[KEY_INDEX], line -> line[VALUE_INDEX]));
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public Map<String, String> getQueryStrings() {
        return startLine.getQueryString();
    }

    public String getProtocol() {
        return startLine.getProtocol();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
