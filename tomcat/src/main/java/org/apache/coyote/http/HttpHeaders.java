package org.apache.coyote.http;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class HttpHeaders {

    private static final String CRLF = "\r\n";
    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private static final int REQUEST_LINE_TOKENS_SIZE = 3;
    private static final int PROTOCOL_INDEX = 2;
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final String MULTIPLE_VALUES_DELIMITER = ",";

    private final String method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final Map<String, List<String>> headerValues;

    private HttpHeaders(String method, String path, Map<String, String> queryParameters, Map<String, List<String>> headerValues) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.headerValues = headerValues;
    }

    public static HttpHeaders from(String header) {
        validateHeader(header);
        String[] requestLineAndHeaderValues = header.split(CRLF, 2);
        String[] requestLineTokens = parseRequestLine(requestLineAndHeaderValues[0]);
        String path = parsePath(requestLineTokens[REQUEST_URI_INDEX]);
        Map<String, String> queryParameters = parseQueryParameters(requestLineTokens[REQUEST_URI_INDEX]);
        Map<String, List<String>> headerValues = parseHeaderValues(requestLineAndHeaderValues[1]);

        return new HttpHeaders(requestLineTokens[METHOD_INDEX], path, queryParameters, headerValues);
    }

    private static void validateHeader(String header) {
        if (header == null || header.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    private static String[] parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        validateRequestLine(tokens);
        return tokens;
    }

    private static void validateRequestLine(String[] requestLineTokens) {
        if (requestLineTokens.length != REQUEST_LINE_TOKENS_SIZE) {
            throw new IllegalArgumentException();
        }
        if (!requestLineTokens[PROTOCOL_INDEX].equals(PROTOCOL_VERSION)) {
            throw new IllegalArgumentException();
        }
    }

    private static String parsePath(String requestUri) {
        return requestUri.split("\\?")[0];
    }

    private static Map<String, String> parseQueryParameters(String requestUri) {
        String[] split = requestUri.split("\\?");
        try {
            if (1 < split.length) {
                return Arrays.stream(split[1].split("&"))
                        .map(queryParam -> queryParam.split("="))
                        .collect(toMap(keyAndValue -> keyAndValue[0], keyAndValue -> keyAndValue[1]));
            }
            return Collections.emptyMap();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }

    private static Map<String, List<String>> parseHeaderValues(String headerValuesString) {
        String[] headerLines = headerValuesString.split(CRLF);

        Map<String, List<String>> headerValues = new HashMap<>();
        for (var headerLine : headerLines) {
            String[] keyAndValue = headerLine.split(HEADER_KEY_VALUE_DELIMITER);
            headerValues.computeIfAbsent(keyAndValue[0], ignored -> new ArrayList<>())
                    .addAll(parseMultipleValues(keyAndValue[1]));
        }

        return headerValues;
    }

    private static List<String> parseMultipleValues(String headerValue) {
        return Arrays.stream(headerValue.split(MULTIPLE_VALUES_DELIMITER))
                .map(String::strip)
                .collect(Collectors.toList());
    }

    public int getContentLength() {
        List<String> contentLength = headerValues.get(HEADER_KEY.CONTENT_LENGTH.value);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength.get(0));
    }

    public String getContentType() {
        List<String> contentType = headerValues.get(HEADER_KEY.CONTENT_TYPE.value);
        if (contentType == null) {
            return null;
        }
        return contentType.get(0);
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public Map<String, List<String>> getHeaderValues() {
        return headerValues;
    }

    public String getPath() {
        return path;
    }

    public enum HEADER_KEY {

        CONTENT_LENGTH("Content-Length"),
        CONTENT_TYPE("Content-Type"),
        ;

        public final String value;

        HEADER_KEY(String value) {
            this.value = value;
        }
    }
}
