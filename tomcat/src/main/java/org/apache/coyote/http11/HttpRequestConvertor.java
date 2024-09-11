package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestBody;
import org.apache.coyote.http11.httprequest.HttpRequestHeader;
import org.apache.coyote.http11.httprequest.HttpRequestLine;

public class HttpRequestConvertor {

    private static final String HEADER_DELIMITER = ":";
    private static final String BODY_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String BODY_DELIMITER = "&";
    private static final String TUPLE_DELIMITER = "=";
    private static final int TUPLE_MIN_LENGTH = 2;
    private static final int TUPLE_KEY_INDEX = 0;
    private static final int TUPLE_VALUE_INDEX = 1;
    private static final int HEADER_KEY_INDEX = 0;

    public static HttpRequest convertHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("요청이 비어 있습니다.");
        }

        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);
        Map<String, String> headers = getHeaders(bufferedReader);
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(headers);

        if (httpRequestHeader.containsHeader(HttpHeaderName.CONTENT_LENGTH)
                && httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_TYPE).equals(BODY_FORM_CONTENT_TYPE)
        ) {
            HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader);
            return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
        }

        return new HttpRequest(httpRequestLine, httpRequestHeader);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(HEADER_DELIMITER);
            headers.put(requestLine[HEADER_KEY_INDEX], parseHeaderValue(requestLine));
        }

        return headers;
    }

    private static String parseHeaderValue(String[] requestLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < requestLine.length; i++) {
            sb.append(requestLine[i].strip());
        }

        return sb.toString();
    }

    private static HttpRequestBody getHttpRequestBody(
            BufferedReader bufferedReader,
            HttpRequestHeader httpRequestHeader
    ) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        Map<String, String> body = extractBody(requestBody);
        return new HttpRequestBody(body);
    }

    private static Map<String, String> extractBody(String requestBody) {
        String[] tokens = requestBody.split(BODY_DELIMITER);
        return Arrays.stream(tokens)
                .filter(token -> token.split(TUPLE_DELIMITER).length >= TUPLE_MIN_LENGTH)
                .map(token -> token.split(TUPLE_DELIMITER))
                .collect(Collectors.toMap(
                        token -> token[TUPLE_KEY_INDEX],
                        token -> token[TUPLE_VALUE_INDEX]
                ));
    }
}
