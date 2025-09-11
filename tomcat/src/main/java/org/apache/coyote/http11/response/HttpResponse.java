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
        final String lineSeparator = "\r\n";

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

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(String value) {
        headers.put("Set-Cookie", value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.statusLine = httpResponse.statusLine;
        this.headers = httpResponse.headers;
        this.body = httpResponse.body;
    }
}
