package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.request.QueryParams;

public class Parser {

    public static final String QUERY_PARAM_REGEX = "&";
    public static final String DELIMITER = "=";
    public static final String COOKIE_REGEX = "; ";

    private Parser() {
    }

    public static QueryParams parseToQueryParams(String line) {
        if (line == null) {
            return QueryParams.empty();
        }
        String[] split = line.split(QUERY_PARAM_REGEX);

        Map<String, String> params = new HashMap<>();
        for (String piece : split) {
            int idx = piece.indexOf(DELIMITER);
            params.put(piece.substring(0, idx), piece.substring(idx + 1));
        }

        return new QueryParams(params);
    }

    public static Cookies parseToCookie(String line) {
        if (line == null) {
            return Cookies.empty();
        }
        String[] split = line.split(COOKIE_REGEX);

        Map<String, String> cookies = new HashMap<>();
        for (String piece : split) {
            int idx = piece.indexOf(DELIMITER);
            cookies.put(piece.substring(0, idx), piece.substring(idx + 1));
        }

        return new Cookies(cookies);
    }
}
