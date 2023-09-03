package org.apache.coyote.http11.http;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.ResponseStatus;

import java.util.List;
import java.util.StringJoiner;

public class ResponseEntity {

    /*
    final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.toString().getBytes().length + " ",
                    "",
                    responseBody);
     */
    private final HttpVersion httpVersion;
    private final ResponseStatus responseStatus;
    private final List<String> responseHeaders;
    private final String responseBody;

    public ResponseEntity(HttpVersion httpVersion, ResponseStatus responseStatus, List<String> responseHeaders, String responseBody) {
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public String generateResponseMessage() {
        return String.join(
                "\r\n",
                generateStatus(),
                generateHeader(),
                "",
                responseBody
        );
    }

    private String generateStatus() {
        return String.join(
                " ",
                httpVersion.getVersion(),
                String.valueOf(responseStatus.getStatusCode()),
                responseStatus.name(),
                ""
        );
    }

    private String generateHeader() {
        StringJoiner headers = new StringJoiner("\r\n");
        for (String header : responseHeaders) {
            headers.add(
                    String.join(" ", header, "")
            );
        }
        return headers.toString();
    }
}
