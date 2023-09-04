package org.apache.coyote.http11.response;

import nextstep.jwp.controller.dto.Response;
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
            final String httpStatus,
            final String contentType,
            final String responseBody
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus + " \r\n",
                "Content-Type: " + contentType + " \r\n" +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n",
                responseBody);
    }

    public static HttpResponse of(
            final HttpVersion httpVersion,
            final Response response,
            final Map<String, String> contentType,
            final String responseBody
    ) {
        return new HttpResponse(httpVersion.getVersion() + " " + response.getHttpStatus() + " \r\n",
//                "Set-Cookie: " + response.getResponseHeader().get("Set-Cookie") + " \r\n" +
                "Content-Type: " + contentType.get("Content-Type") + " \r\n" +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n",
                responseBody);

    }

    public static HttpResponse of(
            final HttpVersion httpVersion,
            final String httpStatus,
            final String redirectUri
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus + " \r\n",
                "Location: " + redirectUri + " \r\n",
                "");
    }

    public static HttpResponse of(
            final HttpVersion httpVersion,
            final String httpStatus,
            final Map<String, String> body,
            final String redirectUri) {
        final String headers = body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "\r\n")
                .collect(Collectors.joining());
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus + " \r\n",
                headers + " \r\n" +
                        "Location: " + redirectUri + " \r\n",
                "");
    }

    public String getResponse() {
        return startLine + headers + "\r\n" + body;
    }
}
