package org.apache.coyote.http11.response;

import java.util.stream.Collectors;

public class HttpResponse {

    private StatusCode statusCode;
    private final ResponseHeader header;
    private String responseBody = "";

    public HttpResponse() {
        header = new ResponseHeader();
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " ",
                printHeader() + "\n",
                responseBody);
    }

    public String printHeader() {
        return header.getHeader().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " \r")
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public void addHeader(final String key,
                          final String value) {
        this.header.addHeader(key, value);
    }

    public void addJSessionId(final String JSessionId) {
        header.addHeader("Set-Cookie", "JSESSIONID=" + JSessionId);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

}
