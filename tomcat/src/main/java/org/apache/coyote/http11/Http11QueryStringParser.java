package org.apache.coyote.http11;

import java.util.LinkedHashMap;

public class Http11QueryStringParser {

    public LinkedHashMap<String, String> parse(String requestUri) {
        if (notHasQueryString(requestUri)) {
            return new LinkedHashMap<>();
        }

        String queryString = requestUri.substring(requestUri.indexOf("?") + 1);
        if (isInValidQueryString(queryString)) {
            return new LinkedHashMap<>();
        }

        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (String singleQueryString : queryString.split("&")) {
            putQueryString(singleQueryString, result);
        }
        return result;
    }

    private boolean notHasQueryString(String requestUri) {
        return !requestUri.contains("?");
    }

    private boolean isInValidQueryString(String queryString) {
        return !queryString.contains("=");
    }

    private void putQueryString(String singleQueryString, LinkedHashMap<String, String> result) {
        String[] split = singleQueryString.split("=");
        result.putLast(split[0], split[1]);
    }
}
