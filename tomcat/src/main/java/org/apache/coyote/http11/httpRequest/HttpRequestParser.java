package org.apache.coyote.http11.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.util.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            String firstLineOfHttpRequest = readOneLineOfInputStream(bufferedReader);
            String[] methodAndUriAndProtocol = parseMethodAndUriAndProtocol(firstLineOfHttpRequest);

            String method = methodAndUriAndProtocol[0];
            String path = UriParser.parsePath(methodAndUriAndProtocol[1]);
            Map<String, String> queryStrings = UriParser.parseQueryStrings(methodAndUriAndProtocol[1]);
            return new HttpRequest(HttpMethod.from(method), path, queryStrings);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            logger.error(exception.getMessage(), exception);
            return null;
        }
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
}
