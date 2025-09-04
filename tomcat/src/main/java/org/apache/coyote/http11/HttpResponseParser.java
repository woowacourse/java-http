package org.apache.coyote.http11;

import org.apache.catalina.domain.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public final class HttpResponseParser {

    private HttpResponseParser() {
    }

    public static String parse(HttpResponse httpResponse) {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(httpResponse.getStartLine());
        joiner.add(parseHeader(httpResponse));
        if (httpResponse.getBody() != null) {
            joiner.add(new String(httpResponse.getBody(), StandardCharsets.UTF_8));
        }

        return joiner.toString();
    }

    private static String parseHeader(HttpResponse httpResponse) {
        StringBuilder builder = new StringBuilder();
        httpResponse.getHeaders()
                .forEach((key, value) -> builder.append(key).append(": ").append(value).append(" \r\n"));
        return builder.toString();
    }
}
