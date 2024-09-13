package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.coyote.http11.common.HttpCookies;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.ProtocolVersion;

public record HttpRequest(
        Method method,
        String path,
        ProtocolVersion protocolVersion,
        HttpParameters parameters,
        HttpHeaders headers,
        HttpCookies cookies,
        String body
) {

    private static final String DELIMITER_COOKIE = "; ";
    private static final String DELIMITER_HEADER = ": ";
    private static final String DELIMITER_SPACE = " ";
    private static final String DELIMITER_VALUE = "=";
    private static final String DELIMITER_PARAMETER_ENTRY = "&";
    private static final String HEADER_NAME_COOKIE = "Cookie";
    private static final int REQUIRED_REQUEST_LINE_ITEMS_COUNT = 3;

    public static HttpRequest parse(List<String> lines) {
        String[] startLineParts = splitRequestLine(lines.getFirst());
        Method method = Method.from(startLineParts[0]);

        String path = "";
        HttpParameters parameters = new HttpParameters();
        Pattern pattern = Pattern.compile("([^?]+)(\\?(.*))?");
        Matcher matcher = pattern.matcher(startLineParts[1]);
        if (matcher.find()) {
            path = matcher.group(1);
            extractParameters(matcher.group(3)).forEach(parameters::put);
        }

        ProtocolVersion protocolVersion = ProtocolVersion.from(startLineParts[2]);
        HttpHeaders headers = extractHeaders(lines);
        HttpCookies cookies = extractCookies(headers.get(HEADER_NAME_COOKIE));

        String body = extractBody(lines);

        return new HttpRequest(method, path, protocolVersion, parameters, headers, cookies, body);
    }

    private static String[] splitRequestLine(String first) {
        String[] result = first.split(DELIMITER_SPACE);
        if (result.length < REQUIRED_REQUEST_LINE_ITEMS_COUNT) {
            throw new IllegalArgumentException("Request line items count is incorrect: " + first);
        }
        return result;
    }

    private static Map<String, String> extractParameters(String query) {
        if (query == null) {
            return Map.of();
        }
        return Arrays.stream(query.split(DELIMITER_PARAMETER_ENTRY))
                .map(keyValue -> keyValue.split(DELIMITER_VALUE))
                .filter(HttpRequest::isLengthTwo)
                .collect(Collectors.toMap(
                        entry -> entry[0],
                        entry -> URLDecoder.decode(entry[1], Charset.defaultCharset())
                ));
    }

    private static HttpHeaders extractHeaders(List<String> lines) {
        int endHeaderIndex = IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).isEmpty())
                .findFirst()
                .orElse(lines.size());

        HttpHeaders httpHeaders = new HttpHeaders();
        lines.stream()
                .limit(endHeaderIndex)
                .map(line -> line.split(DELIMITER_HEADER))
                .filter(HttpRequest::isLengthTwo)
                .forEach(entry -> httpHeaders.put(entry[0], entry[1]));

        return httpHeaders;
    }

    private static boolean isLengthTwo(String[] entry) {
        return entry.length == 2;
    }

    private static HttpCookies extractCookies(String cookieMessage) {
        if (cookieMessage == null) {
            return new HttpCookies();
        }

        HttpCookies httpCookies = new HttpCookies();
        Arrays.stream(cookieMessage.split(DELIMITER_COOKIE))
                .map(cookie -> cookie.split(DELIMITER_VALUE))
                .filter(HttpRequest::isLengthTwo)
                .forEach(entry -> httpCookies.put(entry[0], entry[1]));

        return httpCookies;
    }

    private static String extractBody(List<String> lines) {
        if (lines.size() > 1 && existsBodySeparatorEmptyLine(lines)) {
            return lines.getLast();
        }
        return null;
    }

    private static boolean existsBodySeparatorEmptyLine(List<String> lines) {
        return lines.get(lines.size() - 2).isEmpty();
    }

    public HttpRequest updatePath(String path) {
        return new HttpRequest(method, path, protocolVersion, parameters, headers, cookies, body);
    }

    public Map<String, String> extractUrlEncodedBody() {
        return extractParameters(body);
    }

    public boolean isMethod(Method method) {
        return method.equals(this.method);
    }
}
