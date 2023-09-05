package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {
    private static final String DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE = "Cookie";
    private final Map<String, String> header;

    private RequestHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static RequestHeader convert(BufferedReader bufferedReader) {
        Map<String, String> header = bufferedReader.lines()
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split(DELIMITER))
                .collect(Collectors.toMap(
                        splitLine -> splitLine[KEY_INDEX],
                        splitLine -> splitLine[VALUE_INDEX]
                ));
        return new RequestHeader(header);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getCookie() {
        return header.get(COOKIE);
    }
}
