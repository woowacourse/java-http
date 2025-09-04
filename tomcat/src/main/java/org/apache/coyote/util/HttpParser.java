package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpParser {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);
    private static final String EMPTY_TEXT = "";
    private static final int EOF = -1;

    public static HttpRequest parseToRequest(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            RequestLine requestLine = parseRequestLine(bufferedReader.readLine());
            log.info("요청 라인 = {} 파싱 성공", requestLine);

            String header;
            Map<String, String> requestHeaders = new HashMap<>();
            while (!Objects.equals(header = bufferedReader.readLine(), EMPTY_TEXT)) {
                String[] keyValue = header.split(": ");
                requestHeaders.put(keyValue[0], keyValue[1]);
                log.info("요청 헤더 Key = {}, Value = {} 파싱 완료", keyValue[0], keyValue[1]);
            }
            log.info("모든 요청 헤더 파싱 완료");

            String requestBody = extractRequestBody(bufferedReader, requestHeaders.get("Content-Length"));
            log.info("요청 바디 파싱 완료");

            return HttpRequest.of(requestLine, requestHeaders, requestBody);
        } catch (IOException e) {
            throw new RuntimeException("HttpRequest 파싱에 실패했습니다.", e);
        }
    }

    private static String extractRequestBody(final BufferedReader bufferedReader, final String rawContentLength)
            throws IOException {
        if (rawContentLength == null || Objects.equals(rawContentLength.trim(), "0")) {
            return EMPTY_TEXT;
        }

        int contentLength = Integer.parseInt(rawContentLength.trim());
        char[] bodyBuffers = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = bufferedReader.read(bodyBuffers, totalRead, contentLength - totalRead);
            if (read == EOF) {
                break;
            }
            totalRead += read;
        }
        return new String(bodyBuffers, 0, totalRead);
    }

    private static RequestLine parseRequestLine(final String rawRequestLine) {
        String[] requestLineParts = rawRequestLine.split(" ");
        String method = requestLineParts[0];
        String requestUrl = requestLineParts[1];
        String httpVersion = requestLineParts[2];
        if (!requestUrl.contains("?")) {
            return new RequestLine(method, requestUrl, httpVersion);
        }
        String[] pathAndQuery = requestUrl.split("\\?", 2);
        String requestPath = pathAndQuery[0];
        String queryString = pathAndQuery[1];

        Map<String, String> queryParameters = parseQueryString(queryString);
        return new RequestLine(method, requestPath, queryParameters, httpVersion);
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        String[] parameterPairs = queryString.split("&");
        for (String parameterPair : parameterPairs) {
            String[] keyValue = parameterPair.split("=");
            queryParameters.put(keyValue[0], keyValue[1]);
        }
        return queryParameters;
    }
}
