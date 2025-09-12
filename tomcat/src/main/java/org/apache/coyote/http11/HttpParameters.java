package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpParameters {

    private final Map<String, List<String>> parameters;

    public HttpParameters(final Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public static HttpParameters getAllParameters(
            final HttpRequestStartLine startLine,
            final HttpHeaders headers,
            final String body
    ) {
        Map<String, List<String>> queryParams = parseQueryString(startLine.getQueryString());
        Map<String, List<String>> bodyParams = parseFormParameters(headers, body, startLine.getHttpMethod());
        Map<String, List<String>> params = mergeAllParams(queryParams, bodyParams);
        return new HttpParameters(params);
    }

    public String getFirst(final String key) {
        List<String> list = parameters.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.getFirst();
    }

    private static Map<String, List<String>> parseQueryString(final String qs) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        if (qs == null || qs.isEmpty()) {
            return out;
        }
        String[] pairs = qs.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String key = (eq >= 0) ? pair.substring(0, eq) : pair;
            String val = (eq >= 0) ? pair.substring(eq + 1) : "";
            out.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
        }
        return out;
    }

    private static Map<String, List<String>> parseFormParameters(
            final HttpHeaders headers,
            final String body,
            final String method
    ) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        if (!"POST".equalsIgnoreCase(method)) {
            return out;
        }

        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            return out;
        }
        if (!contentType.startsWith("application/x-www-form-urlencoded")) {
            return out;
        }

        if (body == null || body.isEmpty()) {
            return out;
        }

        out.putAll(parseQueryString(body));
        return out;
    }

    private static Map<String, List<String>> mergeAllParams(
            final Map<String, List<String>> queryParams,
            final Map<String, List<String>> formParams
    ) {
        final Map<String, List<String>> merged = new LinkedHashMap<>();
        addInto(merged, queryParams);
        addInto(merged, formParams);
        return merged;
    }

    private static void addInto(
            final Map<String, List<String>> base,
            final Map<String, List<String>> add
    ) {
        if (add != null) {
            add.forEach((k, vs) -> base.computeIfAbsent(k, v -> new ArrayList<>()).addAll(vs));
        }
    }
}
