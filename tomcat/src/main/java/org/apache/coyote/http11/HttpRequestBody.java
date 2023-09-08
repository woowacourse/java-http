package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBody {

    private static final String EMPTY_STRING = "";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String USER_INFO_DELIMITER = "&";
    private static final String USER_INFO_SEPARATOR = "=";

    private final String body;

    public static HttpRequestBody of(
            final BufferedReader bufferedReader,
            final HttpRequestHeader headers
    ) throws IOException {
        final String requestContentLength = headers.get(CONTENT_LENGTH);
        if (requestContentLength == null) {
            return new HttpRequestBody(EMPTY_STRING);
        }
        final int contentLength = Integer.parseInt(requestContentLength);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpRequestBody(final String body) {
        this.body = body;
    }

    public Map<String, String> parseUserInfos() {
        return Arrays.stream(body.split(USER_INFO_DELIMITER))
                .map(info -> Arrays.asList(info.split(USER_INFO_SEPARATOR)))
                .collect(Collectors.toMap(infos -> infos.get(0), infos -> infos.get(1), (a, b) -> b));
    }

    public String getBody() {
        return body;
    }
}
