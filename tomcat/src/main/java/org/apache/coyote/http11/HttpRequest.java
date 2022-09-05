package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_URL_INDEX = 1;
    private static final String QUERY_STRING_START_DELIMITER = "\\?";
    private static final String QUERY_STRING_START_TEXT = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int PATH_INDEX = 0;
    private static final int PARAMS_INDEX = 1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> requestParams;

    private HttpRequest(final HttpMethod httpMethod, final String path, final Map<String, String> requestParams) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestParams = requestParams;
    }

    public static HttpRequest from(final String request) {
        String[] requestSegment = request.split(" ");
        String url = requestSegment[HTTP_URL_INDEX];
        return new HttpRequest(
                HttpMethod.from(requestSegment[HTTP_METHOD_INDEX]),
                parsePath(url),
                parseRequestParams(url)
        );
    }

    private static String parsePath(final String url) {
        return url.split(QUERY_STRING_START_DELIMITER)[PATH_INDEX];
    }

    private static Map<String, String> parseRequestParams(final String url) {
        if (!url.contains(QUERY_STRING_START_TEXT)) {
            return Map.of();
        }

        String params = url.split(QUERY_STRING_START_DELIMITER)[PARAMS_INDEX];
        return Arrays.stream(params.split(QUERY_STRING_DELIMITER))
                .map(it -> it.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(it -> it[KEY_INDEX], it -> it[VALUE_INDEX]));
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }
}
