package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.httpRequest.HttpRequestHeaders;
import org.apache.coyote.http11.httpRequest.HttpMethod;
import org.apache.coyote.http11.httpRequest.QueryStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriParser {

    private static final Logger logger = LoggerFactory.getLogger(UriParser.class);
    private static final String QUERY_DELIMITER = "?";

    public static HttpRequestHeaders parseHeaders(BufferedReader bufferedReader) throws IOException {
        String firstLineOfHttpRequest = readOneLineOfInputStream(bufferedReader);

        String[] methodAndUriAndProtocol = parseMethodAndUriAndProtocol(firstLineOfHttpRequest);
        HttpMethod method = HttpMethod.from(methodAndUriAndProtocol[0]);
        String path = parsePath(methodAndUriAndProtocol[1]);
        QueryStrings queryStrings = parseQueryStrings(methodAndUriAndProtocol[1]);
        int contentLength = parseContentLength(bufferedReader);

        return new HttpRequestHeaders(method, path, queryStrings, contentLength);
    }

    private static String readOneLineOfInputStream(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null || line.isBlank()) {
            return "";
        }
        return line;
    }

    private static String[] parseMethodAndUriAndProtocol(String rawLine) {
        String[] methodAndUriAndProtocol = rawLine.split(" ");
        validateMethodAndUriAndProtocol(methodAndUriAndProtocol);
        return methodAndUriAndProtocol;
    }

    private static void validateMethodAndUriAndProtocol(String[] methodAndUriAndProtocol) {
        if (methodAndUriAndProtocol.length != 3) {
            IllegalArgumentException exception = new IllegalArgumentException("HTTP 요청 메시지 형식이 잘못되었습니다.");
            logger.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private static String parsePath(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        if (indexOfQueryDelimiter == -1) {
            return uri;
        }
        return uri.substring(0, indexOfQueryDelimiter);
    }

    private static QueryStrings parseQueryStrings(String uri) {
        int indexOfQueryDelimiter = uri.indexOf(QUERY_DELIMITER);
        if (indexOfQueryDelimiter == -1) {
            return new QueryStrings(new HashMap<>());
        }
        Map<String, String> queryStrings = Arrays.stream(uri.substring(indexOfQueryDelimiter + 1).split("&"))
            .map(queryString -> queryString.split("="))
            .collect(Collectors.toMap(
                strings -> decodeValue(strings[0]), // key
                strings -> decodeValue(strings[1]), // value
                (oldValue, newValue) -> newValue
            ));
        return new QueryStrings(queryStrings);
    }

    private static String decodeValue(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static int parseContentLength(BufferedReader bufferedReader) throws IOException {
        int contentLength = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("content-length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
                break;
            }
        }
        return contentLength;
    }

    public static HttpBody parseBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        if (contentLength == 0) {
            return new HttpBody(new HashMap<>());
        }
        skipRestHeaders(bufferedReader);
        String rawBody = readBodyLine(contentLength, bufferedReader);
        Map<String, String> requestBody = Arrays.stream(rawBody.split("&"))
            .map(queryString -> queryString.split("="))
            .collect(Collectors.toMap(
                strings -> decodeValue(strings[0]), // key
                strings -> decodeValue(strings[1]), // value
                (oldValue, newValue) -> newValue
            ));
        return new HttpBody(requestBody);
    }

    private static void skipRestHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            line = bufferedReader.readLine();
        }
    }

    private static String readBodyLine(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
