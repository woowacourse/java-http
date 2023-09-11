package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private final String body;

    public static HttpRequestBody from(final String contentLength, final BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return new HttpRequestBody("");
        }
        int length = Integer.parseInt(contentLength.trim());
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }


    public Map<String, String> parse() {
        Map<String, String> parsedRequestBody = new HashMap<>();
        String[] queryTokens = body.split("&");
        for (String queryToken : queryTokens) {
            putRequestBodyToken(queryToken, parsedRequestBody);
        }
        return parsedRequestBody;
    }

    private void putRequestBodyToken(String queryToken, Map<String, String> parsedRequestBody) {
        int equalSeparatorIndex = queryToken.indexOf("=");
        if (equalSeparatorIndex != -1) {
            parsedRequestBody.put(queryToken.substring(0, equalSeparatorIndex),
                    queryToken.substring(equalSeparatorIndex + 1));
        }
    }

    private HttpRequestBody(final String body) {
        this.body = body;
    }

    public String[] split(final String regex) {
        return body.split(regex);
    }
}
