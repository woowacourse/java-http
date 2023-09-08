package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.start.HttpVersion;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private final String startLine;
    private final String headers;
    private final String body;

    private HttpResponse(final String startLine, final String headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(
            final HttpVersion httpVersion,
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final String responseBody
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus.getStatusName() + " \r\n",
                makeHeader(headers) +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n",
                responseBody);
    }

    public static HttpResponse resourceOf(
            final HttpVersion httpVersion,
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final String responseBody
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus.getStatusName() + " \r\n",
                makeHeader(headers) + " \r\n",
                responseBody);
    }

    public static HttpResponse redirectOf(
            final HttpVersion httpVersion,
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final String responseBody
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus.getStatusName() + " \r\n",
                makeHeader(headers) +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n",
                responseBody);
    }



    private static String makeHeader(final Map<String, String> body) {
        return body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "\r\n")
                .collect(Collectors.joining());
    }

    public String getResponse() {
        return startLine + headers + "\r\n" + body;
    }
}
