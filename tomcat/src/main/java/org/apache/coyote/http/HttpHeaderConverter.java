package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHeaderConverter {

    private static final Logger log = LoggerFactory.getLogger(HttpHeaderConverter.class);

    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final String MULTIPLE_VALUES_DELIMITER = ",";
    private static final String CRLF = "\r\n";

    private HttpHeaderConverter() {
    }

    public static HttpHeader decode(BufferedReader bufferedReader) {
        Map<String, List<String>> headers = new HashMap<>();

        try {
            String headerLine;
            while (!"".equals(headerLine = bufferedReader.readLine()) && headerLine != null) {
                String[] keyValue = headerLine.split(HEADER_KEY_VALUE_DELIMITER);
                validateLength(keyValue);
                String key = keyValue[0];
                String value = keyValue[1];

                List<String> values = headers.computeIfAbsent(key, ignored -> new ArrayList<>());
                values.addAll(decodeMultipleValues(value));
            }
            return HttpHeader.from(headers);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static List<String> decodeMultipleValues(String headerValue) {
        return Arrays.stream(headerValue.split(MULTIPLE_VALUES_DELIMITER))
                     .map(String::strip)
                     .collect(Collectors.toList());
    }

    private static void validateLength(String[] keyValue) {
        if (keyValue.length != 2) {
            String header = String.join(HEADER_KEY_VALUE_DELIMITER, keyValue);
            log.error("헤더 키, 값 오류: {}", header);
        }
    }

    public static String encode(HttpHeader httpHeader) {
        Map<String, List<String>> headers = httpHeader.getHeaders();

        return headers.entrySet()
                      .stream()
                      .map(header -> header.getKey()
                          + HEADER_KEY_VALUE_DELIMITER
                          + String.join(MULTIPLE_VALUES_DELIMITER, header.getValue())
                      ).collect(Collectors.joining(CRLF)) + CRLF + CRLF;
    }
}
