package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String pathUri;
    private final String protocolVersion;
    private final Map<String, String> requestHeaders;
    private final String requestBody;
    private final String queryParams;

    private HttpRequest(String method, String pathUri, String protocolVersion, Map<String, String> requestHeaders, String requestBody, String queryParams) {
        this.method = method;
        this.pathUri = pathUri;
        this.protocolVersion = protocolVersion;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(BufferedInputStream input) throws IOException {
        return parse(input);
    }

    public String getHeader(String header) {
        return requestHeaders.get(header);
    }

    public Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> queries = new HashMap<>();

        if (queryParams == null || queryParams.isBlank()) {
            return queries;
        }

        for (String parameter : queryParams.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }
            queries.put(keyValue[0], keyValue[1]);
        }

        return queries;
    }

    private static HttpRequest parse(BufferedInputStream input) throws IOException {
        String headerFirstLine = readLine(input);

        String method = headerFirstLine.split(" ")[0];
        String requestPath = headerFirstLine.split(" ")[1];
        String protocolVersion = headerFirstLine.split(" ")[2];

        int queryIndex = requestPath.indexOf("?");
        String pathUri = readPathUri(requestPath, queryIndex);
        String queryParams = readQuery(requestPath, queryIndex);

        Map<String, String> headers = readHeaders(input);
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        String reqBody = readReqBody(input, contentLength);

        return new HttpRequest(
                method,
                pathUri,
                protocolVersion,
                headers,
                reqBody,
                queryParams
        );
    }

    private static String readLine(BufferedInputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        int current;
        boolean isCarriageReturn = false;

        while ((current = inputStream.read()) != -1) {
            if (isCarriageReturn) {
                if (current == '\n') {
                    return line.toString(StandardCharsets.ISO_8859_1);
                }
                line.write('\r');
                isCarriageReturn = false;
            }
            if (current == '\r') {
                isCarriageReturn = true;
            } else {
                line.write(current);
            }
        }

        throw new EOFException("라인이 끝나기 전에 스트림이 종료되었습니다.");
    }

    private static String readReqBody(BufferedInputStream bufferedInputStream, int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }

        byte[] bytes = bufferedInputStream.readNBytes(contentLength);

        if (bytes.length != contentLength) {
            throw new EOFException("요청 바디가 중간에 끝났습니다.");
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static Map<String, String> readHeaders(BufferedInputStream bufferedInputStream) throws IOException {
        Map<String, String> headerMaps = new HashMap<>();
        String line;

        while (!(line = readLine(bufferedInputStream)).isEmpty()) {
            headerMaps.put(line.split(":", 2)[0].trim(), line.split(":")[1].trim());
        }

        return headerMaps;
    }

    private static String readQuery(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return "";
        }
        return reqUri.substring(queryIndex + 1);
    }

    private static String readPathUri(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return reqUri;
        }
        return reqUri.substring(0, queryIndex);
    }

    public String getMethod() {
        return method;
    }

    public String getPathUri() {
        return pathUri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getQueryParams() {
        return queryParams;
    }
}
