package com.http.application;

import com.http.domain.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public final class HttpResponseParser {

    private HttpResponseParser() {
    }

    public static String parse(HttpResponse httpResponse) {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(httpResponse.startLine());
        joiner.add(parseHeader(httpResponse));
        joiner.add(new String(httpResponse.body(), StandardCharsets.UTF_8));

        return joiner.toString();
    }


    private static String parseHeader(HttpResponse httpResponse) {
        StringBuilder builder = new StringBuilder();
        httpResponse.headers().forEach((key, value) -> builder.append(key).append(": ").append(value).append(" \r\n"));
        return builder.toString();
    }
}
