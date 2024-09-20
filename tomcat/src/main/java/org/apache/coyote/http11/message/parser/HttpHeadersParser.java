package org.apache.coyote.http11.message.parser;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.common.HttpHeaders;

public class HttpHeadersParser {

    private static final String DELIMITER = ": ";
    private static final int PAIR = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private HttpHeadersParser() {
    }

    public static HttpHeaders parse(List<String> lines) {
        Map<HttpHeader, String> headers = new EnumMap<>(HttpHeader.class);
        lines.forEach(line -> add(headers, line));
        return new HttpHeaders(headers);
    }

    private static void add(Map<HttpHeader, String> headers, String line) {
        String[] content = line.split(DELIMITER);

        if (content.length != PAIR) {
            throw new IllegalArgumentException("잘못된 header 형식입니다.");
        }
        headers.put(
                HttpHeader.from(content[KEY_INDEX].strip()),
                content[VALUE_INDEX].strip()
        );
    }
}
