package org.apache.coyote.httpHeader;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpHeaderParser {

    private static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\S+)$");

    protected static HttpMethod findHttpMethod(final String requestLine) {
        validateRequestLine(requestLine);
        final StringTokenizer stringTokenizer = new StringTokenizer(requestLine);
        return HttpMethod.findHttpMethod(stringTokenizer.nextToken());
    }

    protected static String findPath(final String requestLine) {
        validateRequestLine(requestLine);
        final StringTokenizer stringTokenizer = new StringTokenizer(requestLine);
        stringTokenizer.nextToken();
        return stringTokenizer.nextToken();
    }

    protected static String findProtocol(final String requestLine) {
        validateRequestLine(requestLine);
        final StringTokenizer stringTokenizer = new StringTokenizer(requestLine);
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        return stringTokenizer.nextToken();
    }

    protected static Map<String, String> getHeaders(final List<String> headers) {
        return headers.stream()
                .map(header -> header.split(": ", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    private static void validateRequestLine(final String requestLine) {
        final Matcher matcher = REQUEST_LINE_PATTERN.matcher(requestLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("requestLine의 형식이 잘못되었습니다.");
        }
    }
}
