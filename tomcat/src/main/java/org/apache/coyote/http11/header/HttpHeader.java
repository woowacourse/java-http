package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeader {

    public static final String HEADER_DELIMITER = ": ";
    private static final int REQUEST_LINE_INDEX = 1;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader from(List<String> request) {
        Map<String, String> headers = request.stream()
                .skip(REQUEST_LINE_INDEX)
                .map(HttpHeader::parseHeaderPair)
                .collect(Collectors.toMap(
                                header -> header[HEADER_NAME_INDEX],
                                header -> header[HEADER_VALUE_INDEX].trim()
                        )
                );

        return new HttpHeader(headers);
    }

    private static String[] parseHeaderPair(String headerPair) {
        String[] header = headerPair.split(HEADER_DELIMITER);
        if (header.length != 2) {
            throw new IllegalArgumentException("요청 헤더는 key=value 형식이어야 합니다.");
        }
        return header;
    }

    public void addHeader(HttpHeaderName headerName, String value) {
        headers.put(headerName.getHeaderName(), value);
    }

    public String getHeader(HttpHeaderName headerName) {
        return headers.get(headerName.getHeaderName());
    }

    public String toHttpHeader() {
        List<String> httpHeader = new ArrayList<>();

        for (Entry<String, String> entry : headers.entrySet()) {
            httpHeader.add(entry.getKey() + HEADER_DELIMITER + entry.getValue() + " ");
        }

        return String.join("\r\n", httpHeader);
    }
}
