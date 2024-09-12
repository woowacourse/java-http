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
import org.apache.coyote.http11.ProtocolVersion;

public record HttpRequest(
        Method method,
        String path,
        ProtocolVersion protocolVersion,
        Map<String, String> parameters,
        Map<String, String> headers,
        Map<String, String> cookies,
        String body) {

    private static final String DELIMITER_COOKIE = "; ";
    private static final String DELIMITER_HEADER = ": ";
    private static final String DELIMITER_SPACE = " ";
    private static final String DELIMITER_VALUE = "=";
    private static final String DELIMITER_PARAMETER_ENTRY = "&";
    private static final String HEADER_NAME_COOKIE = "Cookie";

    public static HttpRequest parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(DELIMITER_SPACE);
        Method method = Method.from(startLineParts[0]);

        String path = "";
        Map<String, String> parameters = Map.of();
        Pattern pattern = Pattern.compile("([^?]+)(\\?(.*))?");
        Matcher matcher = pattern.matcher(startLineParts[1]);
        if (matcher.find()) {
            path = matcher.group(1);
            parameters = extractParameters(matcher.group(3));
        }

        ProtocolVersion protocolVersion = ProtocolVersion.from(startLineParts[2]);
        Map<String, String> headers = extractHeaders(lines);
        Map<String, String> cookies = extractCookies(headers.get(HEADER_NAME_COOKIE));

        String body = extractBody(lines);

        return new HttpRequest(method, path, protocolVersion, parameters, headers, cookies, body);
    }

    private static Map<String, String> extractParameters(String query) {
        if (query == null) {
            return Map.of();
        }
        return Arrays.stream(query.split(DELIMITER_PARAMETER_ENTRY))
                .map(keyValue -> keyValue.split(DELIMITER_VALUE))
                .filter(entry -> entry.length == 2)
                .collect(Collectors.toMap(
                        entry -> entry[0],
                        entry -> URLDecoder.decode(entry[1], Charset.defaultCharset())
                ));
    }

    private static Map<String, String> extractHeaders(List<String> lines) {
        int endHeaderIndex = IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).isEmpty())
                .findFirst()
                .orElse(lines.size());

        return lines.stream()
                .limit(endHeaderIndex)
                .map(String::trim)
                .map(line -> line.split(DELIMITER_HEADER))
                .filter(entry -> entry.length == 2)
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
    }

    private static Map<String, String> extractCookies(String cookieMessage) {
        if (cookieMessage == null) {
            return Map.of();
        }

        return Arrays.stream(cookieMessage.split(DELIMITER_COOKIE))
                .map(cookie -> cookie.split(DELIMITER_VALUE))
                .filter(entry -> entry.length == 2)
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1].trim()));
    }

    private static String extractBody(List<String> lines) {
        if (lines.size() > 1 && lines.get(lines.size() - 2).isEmpty()) {
            return lines.getLast();
        }
        return null;
    }

    public HttpRequest updatePath(String path) {
        return new HttpRequest(method, path, protocolVersion, parameters, headers, cookies, body);
    }

    public Map<String, String> extractUrlEncodedBody() {
        return extractParameters(body);
    }

    public boolean hasParameters(List<String> keys) {
        return keys.stream().allMatch(parameters::containsKey);
    }

    public boolean isMethod(Method method) {
        return method.equals(this.method);
    }
}
