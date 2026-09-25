package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> requestHeaders;
    private final String requestBody;

    private HttpRequest(RequestLine requestLine, Map<String, String> requestHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedInputStream input) throws IOException {
        return parse(input);
    }

    public String getHeader(String header) {
        return requestHeaders.get(header);
    }

    private static HttpRequest parse(BufferedInputStream input) throws IOException {
        String headerFirstLine = readLine(input);

        String method = headerFirstLine.split(" ")[0];
        String requestPath = headerFirstLine.split(" ")[1];
        String protocolVersion = headerFirstLine.split(" ")[2];

        Map<String, String> headers = readHeaders(input);
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

        String reqBody = readReqBody(input, contentLength);

        return new HttpRequest(
                new RequestLine(HttpMethod.from(method), requestPath, protocolVersion),
                headers,
                reqBody
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
            headerMaps.put(line.split(":", 2)[0].trim(), line.split(":", 2)[1].trim());
        }

        return headerMaps;
    }

    private Map<String, String> parseUrlEncodedParams(String value) {
        Map<String, String> queries = new HashMap<>();

        if (value == null || value.isBlank()) {
            return queries;
        }

        for (String parameter : value.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }
            queries.put(keyValue[0], keyValue[1]);
        }

        return queries;
    }

    public Map<String, String> getQueryParams() {
        return parseUrlEncodedParams(requestLine.getQueryString());
    }

    public Map<String, String> getFormParams() {
        return parseUrlEncodedParams(requestBody);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPathUri() {
        return requestLine.getPathUri();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getRequestBody() {
        return requestBody;
    }
}
