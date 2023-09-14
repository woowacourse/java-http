package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static final String DELIMITER = ":";
    private static final String PARAMS_DELIMITER = "&";
    private static final String PARAM_VALUE_DELIMITER = "=";

    private static final int PROPERTY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int START_AFTER_SPACE = 2;

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequestLine requestLine = parseRequestLine(reader);
        HttpRequestHeaders headers = parseHeaders(reader);
        HttpRequestBody body = parseBody(reader, headers.getContentLength());

        return new HttpRequest(requestLine, headers, body);
    }

    private static HttpRequestLine parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("요청에 Reqeust-line이 없습니다.");
        }
        return HttpRequestLine.from(requestLine);
    }

    private static HttpRequestHeaders parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int indexOfDelimiter = getIndexOfDelimiter(headerLine);

            String property = headerLine.substring(0, indexOfDelimiter);
            String value = headerLine.substring(indexOfDelimiter + START_AFTER_SPACE);
            headers.put(property, value);
        }

        return new HttpRequestHeaders(headers);
    }

    private static int getIndexOfDelimiter(String headerLine) {
        int indexOfDelimiter = headerLine.indexOf(DELIMITER);

        if (indexOfDelimiter == -1) {
            throw new IllegalArgumentException("요청의 헤더가 잘못되었습니다.");
        }
        return indexOfDelimiter;
    }

    private static HttpRequestBody parseBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return new HttpRequestBody(new HashMap<>());
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        Map<String, String> body = parse(requestBody);

        if (body.size() != requestBody.split(PARAMS_DELIMITER).length) {
            throw new IllegalArgumentException("요청의 바디가 잘못되었습니다.");
        }

        return new HttpRequestBody(body);
    }

    private static Map<String, String> parse(String requestBody) {
        return Arrays.stream(requestBody.split(PARAMS_DELIMITER))
                .map(property -> property.split(PARAM_VALUE_DELIMITER))
                .filter(propertyAndValue -> propertyAndValue.length == 2)
                .collect(Collectors.toMap(data -> data[PROPERTY_INDEX], data -> data[VALUE_INDEX]));
    }

    private HttpRequestParser() {
    }
}
