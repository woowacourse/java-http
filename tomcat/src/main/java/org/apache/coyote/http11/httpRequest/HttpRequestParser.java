package org.apache.coyote.http11.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);
    private static final String QUERY_DELIMITER = "?";

    public static HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            RequestLine requestLine = parseRequestLine(bufferedReader);
            HttpHeaders headers = parseHeaders(bufferedReader);
            HttpBody body = parseBody(headers, bufferedReader);
            return new HttpRequest(requestLine, headers, body);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            return null;
        }
    }

    private static RequestLine parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String rawRequestLine = readOneLineOfInputStream(bufferedReader);
        String[] splittedRequestLine = rawRequestLine.split(" ");
        validateMethodAndUriAndProtocol(splittedRequestLine);
        HttpMethod method = HttpMethod.from(splittedRequestLine[0]);
        String path = parsePath(splittedRequestLine[1]);
        QueryStrings queryStrings = parseQueryStrings(splittedRequestLine[1]);
        HttpProtocolVersion protocolVersion = HttpProtocolVersion.from(splittedRequestLine[2]);
        return new RequestLine(method, path, queryStrings, protocolVersion);
    }

    private static String readOneLineOfInputStream(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null || line.isBlank()) {
            return "";
        }
        return line;
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

    private static HttpHeaders parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while(!(headerLine = bufferedReader.readLine()).isEmpty()) {
            String[] headerEntry = headerLine.split(": ");
            headers.put(headerEntry[0], headerEntry[1]);
        }
        return new HttpHeaders(headers);
    }

    private static HttpBody parseBody(HttpHeaders headers, BufferedReader bufferedReader) throws IOException {
        int contentLength = 0;
        String rawContentLength = headers.getHeaderValueOf("Content-Length");
        if (rawContentLength != null) {
            contentLength = Integer.parseInt(rawContentLength);
        }
        if (contentLength == 0) {
            return new HttpBody(new HashMap<>());
        }

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

    private static String readBodyLine(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
