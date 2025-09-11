package org.apache.coyote.http11;

import com.techcourse.presentation.HttpRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpRequestParser {

    public static Map<String, String> parseFormData(final String body) {
        return parseParameters(body);
    }

    public static Map<String, String> parseQueryString(final String queryString) {
        return parseParameters(queryString);
    }

    public static HttpRequest parseHttpRequest(final List<String> headers, final String body) {
        final String requestLine = headers.getFirst();
        final String[] parts = requestLine.split(" ");

        final String method = parts[0];
        final String url = parts[1];
        final String path = url.split("\\?")[0];
        final String protocol = parts[2].trim();

        final Map<String, String> requestParams = new HashMap<>();

        if (url.contains("?")) {
            final String queries = url.split("\\?")[1];
            requestParams.putAll(parseQueryString(queries));
        }

        if ("POST".equals(method) && body != null && !body.isEmpty()) {
            requestParams.putAll(parseFormData(body));
        }

        final Map<String, String> requestHeaders = new HashMap<>();
        for (int i = 1; i < headers.size(); ++i) {
            final String header = headers.get(i);
            requestHeaders.put(header.split(":")[0].trim(), header.split(":")[1].trim());
        }

        return new HttpRequest(method, path, protocol, requestParams, requestHeaders);
    }

    private static Map<String, String> parseParameters(final String parameterString) {
        final Map<String, String> params = new HashMap<>();

        if (parameterString == null || parameterString.isEmpty()) {
            return params;
        }

        for (String pair : parameterString.split("&")) {
            final int index = pair.indexOf('=');
            if (index == -1) {
                continue;
            }

            final String key = URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }

        return params;
    }

    private HttpRequestParser() {
    }
}
