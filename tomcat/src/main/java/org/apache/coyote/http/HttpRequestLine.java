package org.apache.coyote.http;

import static org.apache.coyote.http.HttpConstants.HTTP_PROTOCOL_PREFIX;
import static org.apache.coyote.http.HttpConstants.KEY_VALUE_SEPARATOR;
import static org.apache.coyote.http.HttpConstants.PARAM_SEPARATOR;
import static org.apache.coyote.http.HttpConstants.QUERY_STRING;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpRequestLine {

    public static final int HTTP_METHOD_INDEX = 0;
    public static final int HTTP_PATH_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;
    public static final int MIN_REQUEST_LINE_PARTS = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> queryParams;

    public static HttpRequestLine from(final String rawRequestLine) {
        final String[] parts = parseRequestLine(rawRequestLine);

        final HttpMethod method = HttpMethod.from(parts[HTTP_METHOD_INDEX]);
        final String fullPath = parts[HTTP_PATH_INDEX];
        final String version = parts[HTTP_VERSION_INDEX].replace(HTTP_PROTOCOL_PREFIX, "");

        final int queryIndex = fullPath.indexOf(QUERY_STRING);
        final String path = parsePath(fullPath, queryIndex);
        final Map<String, String> queryParams = parseQueryParams(fullPath, queryIndex);

        return new HttpRequestLine(method, path, version, queryParams);
    }

    private static String[] parseRequestLine(final String requestLine) {
        final String[] parts = requestLine.trim().split("\\s+");
        validateRequestLine(parts);
        return parts;
    }

    private static void validateRequestLine(final String[] parts) {
        if (parts.length < MIN_REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException(
                    "HTTP 요청의 첫 번째 줄은 " + MIN_REQUEST_LINE_PARTS + "개의 부분으로 이뤄져야 합니다: "
                            + "<메서드> <요청 대상> <HTTP 버전>");
        }

        if (!parts[2].contains(HTTP_PROTOCOL_PREFIX)) {
            throw new IllegalArgumentException("HTTP 요청의 첫 번째 줄은 " + HTTP_PROTOCOL_PREFIX + "를 포함해야 합니다");
        }
    }

    private static String parsePath(final String fullPath, final int queryIndex) {
        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    private static Map<String, String> parseQueryParams(final String fullPath, final int queryIndex) {
        final Map<String, String> queryParams = new HashMap<>();

        if (queryIndex != -1) {
            final String queryString = fullPath.substring(queryIndex + 1);
            parseQueryString(queryString, queryParams);
        }

        return queryParams;
    }

    private static void parseQueryString(final String queryString, final Map<String, String> queryParams) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        final String[] pairs = queryString.split(PARAM_SEPARATOR);
        for (final String pair : pairs) {
            parseKeyValuePair(pair, queryParams);
        }
    }

    private static void parseKeyValuePair(final String pair, final Map<String, String> params) {
        final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = URLDecoder.decode(pair.substring(0, equalIndex), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(equalIndex + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }
    }

    public String getQueryParam(final String name) {
        return queryParams.getOrDefault(name, "");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(path);

        if (!queryParams.isEmpty()) {
            sb.append("?");
            queryParams.forEach((key, value) ->
                    sb.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(PARAM_SEPARATOR));
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(" ").append(HTTP_PROTOCOL_PREFIX).append(version);
        return sb.toString();
    }
}
