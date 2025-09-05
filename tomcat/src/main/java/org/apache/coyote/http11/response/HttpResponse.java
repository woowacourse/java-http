package org.apache.coyote.http11.response;

import java.util.Map;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages#http_responses"></a>
 */
public class HttpResponse {

    private StatusLine statusLine;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(StatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public byte[] toBytes() {
        final String lineSeparator = System.lineSeparator();

        StringBuilder responseBuilder = new StringBuilder();

        // status Line
        responseBuilder.append(
                String.join(" ",
                        statusLine.getProtocol(),
                        String.valueOf(statusLine.getStatusCode()),
                        statusLine.getReasonPhrase()
                )
        ).append(lineSeparator);

        // headers
        headers.forEach((k, v) ->
                responseBuilder.append(k).append(": ").append(v)
                        .append(lineSeparator)
        );

        // 헤더와 바디 구분을 위한 개행
        responseBuilder.append(lineSeparator);

        // body
        responseBuilder.append(body);

        return responseBuilder.toString().getBytes();
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
