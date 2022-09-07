package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private static final String HTTP_HEADER_REGEX = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> value;

    public HttpHeader(Map<String, String> value) {
        this.value = value;
    }
    public static HttpHeader from(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while(!"".equals(line)) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        return new HttpHeader(parsingHeader(headerLines));
    }

    private static Map<String, String> parsingHeader(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(HTTP_HEADER_REGEX);
            String key = item[HEADER_KEY_INDEX];
            String value = item[HEADER_VALUE_INDEX];
            headers.put(key, value);
        }
        return headers;
    }

    public String getContentLength() {
        return value.getOrDefault("Content-Length", "0");
    }
}
