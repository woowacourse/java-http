package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;

class Http11QueryParser {

    LinkedHashMap<String, String> parse(String requestUri) {
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
        if (split.length != 2) {
            return;
        }
        result.putLast(split[0], split[1]);
    }
}
