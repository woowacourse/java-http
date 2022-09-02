package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.support.HttpStatus;

public class ResponseMessage {

    private final HttpStatus status;
    private String contentType;
    private String messageBody;

    public ResponseMessage(HttpStatus status) {
        this.status = status;
    }

    public ResponseMessage setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ResponseMessage setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public String build() {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(status.toStatusLine());
        for (String header : toHeaders()) {
            joiner.add(header);
        }
        joiner.add("");
        if (messageBody != null && messageBody.length() > 0) {
            joiner.add(messageBody);
        }

        return joiner.toString();
    }

    private List<String> toHeaders() {
        List<String> headers = new ArrayList<>();
        if (contentType != null) {
            headers.add(String.format("Content-Type: %s;charset=utf-8 ", contentType));
        }
        if (messageBody != null) {
            headers.add(String.format("Content-Length: %d ", messageBody.getBytes().length));
        }
        return headers;
    }
}
