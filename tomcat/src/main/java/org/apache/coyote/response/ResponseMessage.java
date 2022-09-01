package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.support.HttpStatus;

public class ResponseMessage {

    private final HttpStatus status;
    private String contentType;
    private String responseBody;

    public ResponseMessage(HttpStatus status) {
        this.status = status;
    }

    public ResponseMessage setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ResponseMessage setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public String build() {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(String.format("HTTP/1.1 %s ", status.toResponse()));
        for (String header : toHeaders()) {
            joiner.add(header);
        }
        joiner.add("");
        joiner.add(responseBody);

        return joiner.toString();
    }

    private List<String> toHeaders() {
        List<String> headers = new ArrayList<>();
        if (contentType != null) {
            headers.add(String.format("Content-Type: %s;charset=utf-8 ", contentType));
        }
        if (responseBody != null) {
            headers.add(String.format("Content-Length: %d ", responseBody.getBytes().length));
        }
        return headers;
    }
}
