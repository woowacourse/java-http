package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestMessageReader {

    private static final String DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_TOKEN_SIZE = 3;
    private static final String QUERY_PARAMETER_START_FLAG = "?";
    private static final String HTTP_HEADER_VALUE_DELIMITER = ":";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_DELIMITER = "=";

    public static HttpRequest readHttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final HttpRequestStartLine startLine = readStartLine(br.readLine());
        final Map<String, String> headers = readHeaders(br);
        if (startLine.getHttpRequestMethod() == HttpRequestMethod.POST) {
            return httpRequestWithBody(headers, br, startLine);
        }
        return HttpRequest.of(startLine, headers);
    }

    public static HttpRequestStartLine readStartLine(final String requestLine) {
        final String[] startLineTokens = requestLine.split(DELIMITER);
        validateStartLineTokenSize(startLineTokens);
        final HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(startLineTokens[HTTP_METHOD_INDEX]);
        final String URIWithQueryStrings = startLineTokens[REQUEST_URI_INDEX];
        final String requestURI = parseURI(URIWithQueryStrings);
        final Map<String, String> queryParams = parseQueryParams(URIWithQueryStrings);
        final String httpVersion = startLineTokens[HTTP_VERSION_INDEX];
        return new HttpRequestStartLine(httpRequestMethod, requestURI, httpVersion, queryParams);
    }

    private static Map<String, String> parseQueryParams(final String URIWithQueryStrings) {
        if (URIWithQueryStrings.contains(QUERY_PARAMETER_START_FLAG)) {
            final int index = URIWithQueryStrings.indexOf(QUERY_PARAMETER_START_FLAG);
            final String params = URIWithQueryStrings.substring(index + 1);
            return Arrays.stream(params.split(PARAMETER_SEPARATOR))
                    .map(param -> param.split(QUERY_PARAMETER_DELIMITER))
                    .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }
        return Map.of();
    }

    private static String parseURI(final String URIWithQueryStrings) {
        if (URIWithQueryStrings.contains(QUERY_PARAMETER_START_FLAG)) {
            final int index = URIWithQueryStrings.indexOf(QUERY_PARAMETER_START_FLAG);
            return URIWithQueryStrings.substring(0, index);
        }
        return URIWithQueryStrings;
    }

    private static void validateStartLineTokenSize(final String[] lines) {
        if (lines.length != START_LINE_TOKEN_SIZE) {
            throw new IllegalArgumentException("시작 라인의 토큰은 3개여야 합니다.");
        }
    }

    private static Map<String, String> readHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String readLine = br.readLine();
        while (readLine != null && !readLine.isEmpty()) {
            System.out.println(readLine);
            addHeaderByReadLine(readLine, headers);
            readLine = br.readLine();
        }

        return headers;
    }

    private static void addHeaderByReadLine(final String readLine, final Map<String, String> headers) {
        if (readLine == null || readLine.isEmpty()) {
            return;
        }

        if (readLine.contains(HTTP_HEADER_VALUE_DELIMITER)) {
            final int index = readLine.indexOf(HTTP_HEADER_VALUE_DELIMITER);
            final String headerKey = readLine.substring(0, index).strip();
            final String headerValue = readLine.substring(index + 1).strip();
            headers.put(headerKey, headerValue);
            return;
        }

        throw new IllegalStateException("헤더는 key: value 형태여야 합니다.");
    }

    private static HttpRequest httpRequestWithBody(
            final Map<String, String> headers,
            final BufferedReader br,
            final HttpRequestStartLine startLine
    ) throws IOException {
        final SupportPayloadContentType supportPayloadContentType =
                SupportPayloadContentType.from(headers.get("Content-Type"));
        final String body = readBody(br, Integer.parseInt(headers.get("Content-Length")));
        final PayloadParser payloadParser = supportPayloadContentType.getPayloadParser();
        Map<String, String> payload = payloadParser.parse(body);
        return HttpRequest.of(startLine, headers, payload);
    }

    private static String readBody(final BufferedReader br, final int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
