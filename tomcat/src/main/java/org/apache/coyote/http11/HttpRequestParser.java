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

    public HttpRequest accept(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final var startLine = bufferedReader.readLine();
        final var method = startLine.split(START_LINE_DELIMITER)[0];
        final var path = getPath(startLine);
        final var headers = readHeader(bufferedReader);
        final var body = readMessageBody(bufferedReader, headers);
        final var cookies = findCookies(headers);

        return new HttpRequest(method, path, headers, body, cookies);
    }

    private Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] split = line.split(":");
            header.put(split[KEY_INDEX], split[VALUE_INDEX].trim());
            line = bufferedReader.readLine();
        }
        return header;
    }

    private String readMessageBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String key = "Content-Length";
        if (headers.containsKey(key)) {
            int contentLength = Integer.parseInt(headers.get(key));
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            return new String(body);
        }
        return "";
    }

    public Map<String, String> findCookies(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().equals("Cookie"))
                .map(entry -> entry.getValue().split("; "))
                .flatMap(Arrays::stream)
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public Map<String, String> findQueryStrings(String path) {
        if (!path.contains(QUERY_PARAMETER_DELIMITER)) {
            return new HashMap<>();
        }
        String queryString = path.split(QUERY_PARAMETER_DELIMITER_REGEX)[1];
        return Arrays.stream(queryString.split("&"))
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public String getPath(String startLine) {
        String url = startLine.split(START_LINE_DELIMITER)[1];
        if (!startLine.contains(QUERY_PARAMETER_DELIMITER)) {
            return url;
        }
        return url.split(QUERY_PARAMETER_DELIMITER_REGEX)[0];
    }


}
