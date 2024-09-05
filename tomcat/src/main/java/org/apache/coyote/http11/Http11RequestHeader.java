package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String EMPTY_STRING = "";
    private static final String HEADER_VALUE_DELIMITER = ",";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final StartLine startLine;
    private final HttpHeaders httpHeaders;

    public Http11RequestHeader(StartLine startLine, HttpHeaders httpHeaders) {
        this.startLine = startLine;
        this.httpHeaders = httpHeaders;
    }

    public static Http11RequestHeader from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StartLine startLine = StartLine.from(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.of(extractRequestHeader(bufferedReader), (s1, s2) -> true);

        return new Http11RequestHeader(startLine, httpHeaders);
    }

    private static Map<String, List<String>> extractRequestHeader(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !EMPTY_STRING.equals(line))
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(parts -> parts[HEADER_KEY_INDEX], Http11RequestHeader::extractHeaderValues));
    }

    private static List<String> extractHeaderValues(String[] parts) {
        return Arrays.stream(parts[HEADER_VALUE_INDEX].split(HEADER_VALUE_DELIMITER))
                .map(String::trim)
                .toList();
    }

    public RequestUri getRequestUri() {
        return startLine.getRequestUri();
    }

    public HttpVersion getHttpVersion() {
        return startLine.getHttpVersion();
    }

    public List<String> getAcceptType() {
        return httpHeaders.allValues("Accept");
    }
}
