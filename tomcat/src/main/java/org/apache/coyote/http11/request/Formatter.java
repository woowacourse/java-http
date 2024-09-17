package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Formatter {

    private static final int OFFSET = 0;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SIZE = 2;
    private static final int REQUEST_LINE_SIZE = 3;
    private static final String HEADER_DELIMITER = ":";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_DELIMITER = "=";
    private static final String SPACE = " ";

    public static String[] toRequestLineFormat(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("빈 요청 라인은 처리할 수 없습니다.");
        }

        String[] line = requestLine.split(SPACE);

        if (line.length != REQUEST_LINE_SIZE) {
            throw new IllegalArgumentException("RequestLine의 길이가 올바르지 않습니다.");
        }
        return line;
    }

    public static RequestHeader toHeader(BufferedReader bufferedReader) throws IOException {
        RequestHeader requestHeader = new RequestHeader();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerLine = line.split(HEADER_DELIMITER);

            String key = URLDecoder.decode(headerLine[KEY_INDEX]).trim();
            String value = URLDecoder.decode(headerLine[VALUE_INDEX]).trim();

            requestHeader.addHeader(key, value);
        }
        return requestHeader;
    }

    public static RequestBody toBody(BufferedReader bufferedReader, boolean isGet, int contentLength) throws IOException {
        if (isGet) {
            return RequestBody.empty();
        }

        StringBuilder stringBuilder = toRawBodyValue(bufferedReader, contentLength);

        return new RequestBody(stringBuilder.toString());
    }

    private static StringBuilder toRawBodyValue(BufferedReader bufferedReader, int contentLength) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        char[] buffer = new char[contentLength];

        bufferedReader.read(buffer, OFFSET, contentLength);
        stringBuilder.append(buffer);

        return stringBuilder;
    }

    public static Map<String, String> toBodyValueFormat(String values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(values.split(PARAMETER_DELIMITER))
                .map(param -> param.trim().split(KEY_DELIMITER, SIZE))
                .collect(Collectors.toUnmodifiableMap(
                        result -> result[KEY_INDEX],
                        result -> result[VALUE_INDEX])
                );
    }
}
