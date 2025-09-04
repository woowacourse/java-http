package org.apache.coyote;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();

            validateRequestLine(requestLine);

            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String pathInfo = tokenizer.nextToken();
            String protocol = tokenizer.nextToken();

            String path;
            Map<String, String> params;

            int queryIndex = pathInfo.indexOf('?');
            if (queryIndex == -1) {
                path = pathInfo;
                params = new HashMap<>();
            } else {
                path = pathInfo.substring(0, queryIndex);
                params = parseQueryParams(pathInfo.substring(queryIndex + 1));
            }

            return new HttpRequest(method, path, params, protocol);

        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private static void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
        }
    }

    private static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();

        for (String pair : queryString.split("&")) {
            int equalIndex = pair.indexOf('=');
            if (equalIndex == -1) {
                params.put(pair, "");
                continue;
            }
            params.put(pair.substring(0, equalIndex), pair.substring(equalIndex + 1));
        }

        return params;
    }
}
