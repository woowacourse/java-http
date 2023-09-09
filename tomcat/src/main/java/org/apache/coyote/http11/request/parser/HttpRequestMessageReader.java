package org.apache.coyote.http11.request.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;

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
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private HttpRequestMessageReader() {
    }

    public static HttpRequest readHttpRequest(final InputStream inputStream) throws IOException {
        try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(inputStreamReader)) {

            final HttpRequestLine startLine = readStartLine(br.readLine());
            final HttpHeaders httpHeaders = readHeaders(br);
            final HttpCookie httpCookie = parseCookieFromHeaders(httpHeaders);
            return httpRequestByMethod(startLine, httpHeaders, httpCookie, br);
        }
    }

    private static HttpRequest httpRequestByMethod(
            final HttpRequestLine startLine,
            final HttpHeaders headers,
            final HttpCookie cookie,
            final BufferedReader br
    ) throws IOException {
        if (startLine.getHttpRequestMethod() == HttpMethod.POST) {
            return httpRequestWithBody(startLine, headers, cookie, br);
        }
        return HttpRequest.of(startLine, headers, cookie);
    }

    public static HttpRequestLine readStartLine(final String requestLine) {
        final String[] startLineTokens = requestLine.split(DELIMITER);
        validateStartLineTokenSize(startLineTokens);
        final HttpMethod httpMethod = HttpMethod.from(startLineTokens[HTTP_METHOD_INDEX]);
        final String URIWithQueryStrings = startLineTokens[REQUEST_URI_INDEX];
        final String requestURI = parseURI(URIWithQueryStrings);
        final Map<String, String> queryParams = parseQueryParams(URIWithQueryStrings);
        final String httpVersion = startLineTokens[HTTP_VERSION_INDEX];
        return new HttpRequestLine(httpMethod, requestURI, httpVersion, queryParams);
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

    private static HttpHeaders readHeaders(final BufferedReader br) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String readLine = br.readLine();
        while (readLine != null && !readLine.isEmpty()) {
            addHeaderByReadLine(readLine, headers);
            readLine = br.readLine();
        }

        return HttpHeaders.from(headers);
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

    public static HttpCookie parseCookieFromHeaders(final HttpHeaders headers) {
        final String cookieValues = headers.get("Cookie");
        if (cookieValues == null) {
            return new HttpCookie(Map.of());
        }

        final Map<String, String> cookieKeyValues = Arrays.stream(cookieValues.split(COOKIE_SEPARATOR))
                .map(param -> param.split(COOKIE_DELIMITER))
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        return new HttpCookie(cookieKeyValues);
    }

    private static HttpRequest httpRequestWithBody(
            final HttpRequestLine startLine,
            final HttpHeaders headers,
            final HttpCookie cookie,
            final BufferedReader br
    ) throws IOException {
        final ContentTypePayloadParserMapper contentTypePayloadParserMapper =
                ContentTypePayloadParserMapper.from(headers.get("Content-Type"));
        final String body = readBody(br, Integer.parseInt(headers.get("Content-Length")));
        final PayloadParser payloadParser = contentTypePayloadParserMapper.getPayloadParser();
        final Map<String, String> payload = payloadParser.parse(body);
        return HttpRequest.of(startLine, headers, cookie, payload);
    }

    private static String readBody(final BufferedReader br, final int contentLength) throws IOException {
        final char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
