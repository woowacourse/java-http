package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBody {

    private final String body;

    public static HttpRequestBody of(
            final BufferedReader bufferedReader,
            final HttpRequestHeader headers
    ) throws IOException {
        final String requestContentLength = headers.get("Content-Length");
        if (requestContentLength == null) {
            return new HttpRequestBody("");
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
        return Arrays.stream(body.split("&"))
                .map(info -> Arrays.asList(info.split("=")))
                .collect(Collectors.toMap(infos -> infos.get(0), infos -> infos.get(1), (a, b) -> b));
    }

    public String getBody() {
        return body;
    }

}
