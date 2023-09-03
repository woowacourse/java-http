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
    private static final String START_LINE_DELIMITER = " ";
    private static final String QUERY_PARAMETER_DELIMITER_REGEX = "\\?";
    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private String startLine;
    private Map<String, String> header = new HashMap<>();
    private String messageBody;

    public void accept(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        startLine = bufferedReader.readLine();
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

    public String findMethod() {
        return startLine.split(START_LINE_DELIMITER)[0];
    }

    public String findPath() {
        return startLine.split(START_LINE_DELIMITER)[1];
    }

    public String findProtocol() {
        return startLine.split(START_LINE_DELIMITER)[2];
    }

    public Map<String, String> findCookies() {
        return header.entrySet().stream()
                .filter(entry -> entry.getKey().equals("Cookie"))
                .map(entry -> entry.getValue().split("; "))
                .flatMap(Arrays::stream)
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public Map<String, String> findQueryStrings() {
        String path = findPath();
        if (!path.contains(QUERY_PARAMETER_DELIMITER)) {
            return new HashMap<>();
        }
        String queryString = path.split(QUERY_PARAMETER_DELIMITER_REGEX)[1];
        return Arrays.stream(queryString.split("&"))
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public String findPathWithoutQueryString() {
        String path = findPath();
        if (!path.contains(QUERY_PARAMETER_DELIMITER)) {
            return path;
        }
        return path.split(QUERY_PARAMETER_DELIMITER_REGEX)[0];
    }

    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getMessageBody() {
        return messageBody;
    }

}
