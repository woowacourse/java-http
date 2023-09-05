package org.apache.coyote.http11;

import static nextstep.jwp.Constant.CRLF;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseParser {

    private HttpResponseParser() {
    }

    public static String parse(HttpResponse response) {
        HttpStatus status = response.getHttpStatus();
        return String.join(CRLF,
            parseStartLine(status),
            parseHeaders(response) + parseCookie(response.getCookie()),
            "",
            response.getBody());
    }

    private static String parseStartLine(HttpStatus status) {
        return String.format("HTTP/1.1 %s %s ", status.code, status.name());
    }

    private static String parseHeaders(HttpResponse response) {
        return response.getHeaders().entrySet().stream()
            .map(it -> String.join(": ", it.getKey(), it.getValue() + " "))
            .collect(Collectors.joining(CRLF));
    }

    private static String parseCookie(Map<String, String> cookie) {
        if (cookie.isEmpty()) {
            return "";
        }

        return "\nSet-Cookie: " + cookie.entrySet().stream()
            .map(it -> String.join("=", it.getKey(), it.getValue() + " "))
            .collect(Collectors.joining());
    }
}
