package org.apache.coyote.http11.response;

import org.apache.coyote.http11.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final ResponseLine startLine;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, String responseBody,
                        ContentType contentType, String redirectUrl) {
        this.startLine = new ResponseLine(httpStatus);
        this.headers = new LinkedHashMap<>();
        initHeader(contentType, responseBody, redirectUrl);
        this.responseBody = responseBody;
    }


    public HttpResponse(final HttpStatus httpStatus, final String responseBody, final ContentType contentType) {
        this.startLine = new ResponseLine(httpStatus);
        this.headers = new LinkedHashMap<>();
        initHeader(contentType, responseBody);
        this.responseBody = responseBody;
    }

    private void initHeader(final ContentType contentType, final String responseBody, final String redirectUrl) {
        initHeader(contentType, responseBody);
        headers.put("Location", redirectUrl);
    }

    private void initHeader(ContentType contentType, String responseBody) {
        headers.put("Content-Type", contentType.getContentType() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    public void addJSessionId(final Session session) {
        headers.put("Set-Cookie", "JSESSIONID=" + session.getId());
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                startLine + " ",
                printHeader() + "\n",
                responseBody);
    }

    public String printHeader() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
