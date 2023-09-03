package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.MessageBody;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final MessageBody messageBody;

    private HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final MessageBody messageBody) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponse create(StatusCode code, HttpHeaders headers, String content) {
        return new HttpResponse(StatusLine.create(code), headers, MessageBody.create(content));
    }

    public byte[] getBytes() {
        String status = statusLine.toString();
        String headers = httpHeaders.toString();
        String body = messageBody.getContent();
        return String.join(System.lineSeparator(), status, headers, "", body)
                .getBytes(StandardCharsets.UTF_8);
    }
}
