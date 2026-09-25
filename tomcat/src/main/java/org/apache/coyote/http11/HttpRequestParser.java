package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] requestLineParts = readRequestLineParts(bufferedReader);
        String method = requestLineParts[0];
        String[] requestTargetParts = parseRequestTarget(requestLineParts[1]);
        String version = requestLineParts[2];

        Map<String, String> requestHeaders = readRequestHeaders(bufferedReader);
        int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
        String requestBody = readRequestBody(bufferedReader, contentLength);
        return new HttpRequest(method, requestTargetParts[0], requestTargetParts[1], version, requestHeaders,
                requestBody);
    }

    private static String[] parseRequestTarget(String requestTarget) {
        int queryStart = requestTarget.indexOf('?');
        if (queryStart == -1) {
            return new String[]{requestTarget, ""};
        }
        String path = requestTarget.substring(0, queryStart);
        String queryString = requestTarget.substring(queryStart + 1);
        return new String[]{path, queryString};
    }

    private static String[] readRequestLineParts(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        return requestLine.split(" ");
    }

    private static Map<String, String> readRequestHeaders(BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String requestHeaderLine;
        while ((requestHeaderLine = bufferedReader.readLine()) != null) {
            if (requestHeaderLine.isEmpty()) {
                break;
            }
            String[] header = requestHeaderLine.split(":", 2);
            requestHeaders.put(header[0].trim(), header[1].trim());
        }
        return requestHeaders;
    }

    private static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int readLength = 0;

        while (readLength < contentLength) {
            int currentLength = bufferedReader.read(buffer, readLength, contentLength - readLength);
            if (currentLength == -1) {
                break;
            }
            readLength += currentLength;
        }
        return new String(buffer, 0, readLength);
    }
}
