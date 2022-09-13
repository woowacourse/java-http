package nextstep.jwp.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Parser {

    private static final String START_QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_CONNECT_DELIMITER = "&";
    private static final String COOKIE_CONNECT_DELIMITER = "; ";
    private static final String QUERY_VALUE_DELIMITER = "=";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static String convertResourceFileName(String url) {
        int startQueryIndex = findStartQueryString(url);
        if (startQueryIndex > 0) {
            return url.substring(0, startQueryIndex);
        }
        return url;
    }

    public static Map<String, String> findParam(String url) {
        Objects.nonNull(url);
        int startQueryIndex = findStartQueryString(url);
        if (startQueryIndex < 0) {
            return Collections.emptyMap();
        }
        String queryString = extractQueryString(url, startQueryIndex);
        return parseQueryString(queryString);
    }

    private static int findStartQueryString(String url) {
        return url.indexOf(START_QUERY_STRING_DELIMITER);
    }

    private static String extractQueryString(String url, int startQueryIndex) {
        return url.substring(startQueryIndex + VALUE_INDEX);
    }

    public static Map<String, String> parseQueryString(String queryString) {
        String[] values = queryString.split(QUERY_CONNECT_DELIMITER);
        return toMap(values);
    }

    public static Map<String, String> parseCookieString(String queryString) {
        String[] values = queryString.split(COOKIE_CONNECT_DELIMITER);
        return toMap(values);
    }

    private static Map<String, String> toMap(final String[] values) {
        Map<String, String> params = new HashMap<>();
        for (String param : values) {
            final String[] data = param.split(QUERY_VALUE_DELIMITER);
            params.put(data[NAME_INDEX], data[VALUE_INDEX]);
        }
        return params;
    }

    public static String parseFileType(String fileName) {
        Objects.nonNull(fileName);
        final String fileType = fileName.substring(fileName.indexOf(".") + 1);
        int startQueryIndex = findStartQueryString(fileType);
        if (startQueryIndex > 0) {
            return fileType.substring(0, startQueryIndex);
        }
        return fileType;
    }
}
