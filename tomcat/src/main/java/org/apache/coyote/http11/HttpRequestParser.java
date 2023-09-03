package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String DELIMITER = ":";
    private static final String PARAMS_DELIMITER = "&";
    private static final String PARAM_VALUE_DELIMITER = "=";

    private static final int PROPERTY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int START_AFTER_SPACE = 2;

    private final HttpRequestFirstLineInfo httpRequestFirstLineInfo;
    private final Map<String, String> headers;
    private final Map<String, String> body;


    public HttpRequestParser(
            HttpRequestFirstLineInfo httpRequestFirstLineInfo,
            Map<String, String> headers,
            Map<String, String> body
    ) {
        this.httpRequestFirstLineInfo = httpRequestFirstLineInfo;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequestParser from(BufferedReader reader) throws IOException {
        HttpRequestFirstLineInfo firstLineInfo = parseFirstLine(reader);
        Map<String, String> headers = parseHeaders(reader);
        Map<String, String> body = parseBody(reader, headers);

        return new HttpRequestParser(firstLineInfo, headers, body);
    }

    private static HttpRequestFirstLineInfo parseFirstLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("요청에 헤더가 존재하지 않습니다.");
        }
        return HttpRequestFirstLineInfo.from(requestLine);
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int indexOfDelimiter = headerLine.indexOf(DELIMITER);
            if (indexOfDelimiter != -1) {
                String property = headerLine.substring(0, indexOfDelimiter);
                String value = headerLine.substring(indexOfDelimiter + START_AFTER_SPACE);
                headers.put(property, value);
            }
        }

        return headers;
    }

    private static Map<String, String> parseBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        Map<String, String> body = new HashMap<>();

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);

            String[] bodyProperties = requestBody.split(PARAMS_DELIMITER);
            for (String property : bodyProperties) {
                String[] propertyAndValue = property.split(PARAM_VALUE_DELIMITER);

                if (propertyAndValue.length != 2) {
                    throw new IllegalArgumentException("요청의 바디가 잘못되었습니다.");
                }

                body.put(propertyAndValue[PROPERTY_INDEX], propertyAndValue[VALUE_INDEX]);
            }
        }

        return body;
    }

    public HttpRequestFirstLineInfo getHttpRequestFirstLineInfo() {
        return httpRequestFirstLineInfo;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
