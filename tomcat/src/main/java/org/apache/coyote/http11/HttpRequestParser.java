package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequestParser {

    public HttpRequest parseRequestLine(String line) {
        final String[] tokens = line.split(" ", 3);
        final HttpMethod method = HttpMethod.of(tokens[0]);
        final String uri = tokens[1];

        final String path = getPath(uri);
        final Map<String, String> params = getParams(uri);

        return new HttpRequest(method, path, params);
    }

    private String getPath(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    private Map<String, String> getParams(String uri) {
        return getQueryString(uri)
                .map(this::getParamsMap)
                .orElseGet(Collections::emptyMap);
    }

    private Optional<String> getQueryString(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return Optional.of(uri.substring(index + 1));
        }
        return Optional.empty();
    }

    private Map<String, String> getParamsMap(String queryString) {
        Map<String, String> paramsMap = new HashMap<>();
        String[] data = queryString.split("\\&");
        for (String d : data) {
            String[] param = d.split("\\=");
            paramsMap.put(param[0], param[1]);
        }
        return paramsMap;
    }
}
