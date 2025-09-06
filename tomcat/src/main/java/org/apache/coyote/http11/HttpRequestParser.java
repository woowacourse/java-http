package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpRequest;

public class HttpRequestParser {

    public HttpRequest parse(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();

        final String[] parts = requestLine.split(" ");
        final String method = parts[0];
        String requestUri = parts[1];

        String path = requestUri;
        Map<String, String> queryParams = new HashMap<>();

        if (requestUri.contains("?")) {
            int index = requestUri.indexOf("?");
            path = requestUri.substring(0, index);
            String queryString = requestUri.substring(index + 1);
            queryParams = parseQueryString(queryString);
        }

        return new HttpRequest(method, path, queryParams);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
