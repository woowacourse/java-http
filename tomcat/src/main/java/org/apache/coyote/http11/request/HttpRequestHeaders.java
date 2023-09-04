package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpMessageNotReadableException;

public class HttpRequestHeaders {

    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ":";

    private final Map<String, String> handlerMap;

    private HttpRequestHeaders(final Map<String, String> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public static HttpRequestHeaders of(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeaders = new LinkedHashMap<>();

        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                throw new HttpMessageNotReadableException("HTTP 헤더 필드는 'null'일 수 없습니다.");
            }

            String[] requestHeaderParts = line.split(HEADER_DELIMITER);

            requestHeaders.put(
                    requestHeaderParts[REQUEST_HEADER_KEY_INDEX].strip().toLowerCase(),
                    requestHeaderParts[REQUEST_HEADER_VALUE_INDEX].strip()
            );
        }

        return new HttpRequestHeaders(requestHeaders);
    }

    public HttpRequestHeaders add(String key, String value) {
        handlerMap.put(key.toLowerCase(), value);
        return new HttpRequestHeaders(handlerMap);
    }

    public String getValue(String key) {
        return handlerMap.get(key.toLowerCase());
    }
}
