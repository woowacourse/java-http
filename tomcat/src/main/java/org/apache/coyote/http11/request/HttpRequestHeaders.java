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
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            String[] requestHeaderParts = line.split(HEADER_DELIMITER);

            if (requestHeaderParts.length < 2) {
                throw new HttpMessageNotReadableException("잘못된 HTTP 헤더 형식");
            }

            requestHeaders.put(
                    requestHeaderParts[REQUEST_HEADER_KEY_INDEX].strip().toLowerCase(),
                    requestHeaderParts[REQUEST_HEADER_VALUE_INDEX].strip()
            );
        }

        return new HttpRequestHeaders(requestHeaders);
    }

    public String getValue(String key) {
        return handlerMap.get(key.toLowerCase());
    }
}
