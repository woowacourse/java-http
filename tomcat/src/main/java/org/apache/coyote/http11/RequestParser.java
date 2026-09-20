package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String REQUEST_METHOD = "requestMethod";
    private static final String ENDPOINT = "endpoint";
    private static final String REQUEST_BODY = "requestBody";

    public Map<String, String> parse(BufferedReader reader) {
        try {
            Map<String, String> request = new HashMap<>();

            String requestLine = reader.readLine();
            String[] requestLineTokens = requestLine.split(" ");
            request.put(REQUEST_METHOD, requestLineTokens[0]);
            request.put(ENDPOINT, requestLineTokens[1]);

            String headerLine;
            while (!(headerLine = reader.readLine()).isBlank()) {
                String[] attribute = headerLine.split(":", 2);
                request.put(attribute[0].trim(), attribute[1].trim());
            }

            request.put(REQUEST_BODY, readRequestBody(reader, request));

            return request;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String getRequestPath(Map<String, String> requestHeaderInfos) {
        return removeQueryParamFromRequestEndPoint(
                requestHeaderInfos.get("endpoint")
        );
    }

    public Map<String, String> getQueryParams(Map<String, String> requestHeaderInfos) {
        String requestEndPoint = requestHeaderInfos.get(ENDPOINT);
        int queryStringIndex = requestEndPoint.indexOf('?');

        if (queryStringIndex != -1) {
            return parseQueryParams(requestEndPoint.substring(queryStringIndex + 1));
        }

        return parseQueryParams(requestHeaderInfos.get(REQUEST_BODY));
    }

    public String getAccept(Map<String, String> requestHeaderInfos) {
        return requestHeaderInfos.get("Accept");
    }

    public String getRequestMethod(Map<String, String> requestHeaderInfos) {
        return requestHeaderInfos.get(REQUEST_METHOD);
    }

    public String getCookie(Map<String, String> requestHeaderInfos) {
        return requestHeaderInfos.get("Cookie");
    }

    private String readRequestBody(BufferedReader reader, Map<String, String> requestHeaderInfos) throws IOException {
        int contentLength = Integer.parseInt(requestHeaderInfos.getOrDefault("Content-Length", "0"));
        char[] requestBody = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = reader.read(requestBody, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }

        return new String(requestBody, 0, totalRead);
    }

    private String removeQueryParamFromRequestEndPoint(String requestEndPoint) {
        int queryStringIndex = requestEndPoint.indexOf('?');

        if (queryStringIndex == -1) {
            return requestEndPoint;
        }

        return requestEndPoint.substring(0, queryStringIndex);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();

        for (String pair : query.split("&")) {
            String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length == 2) {
                queryParams.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return queryParams;
    }
}
