package org.apache.coyote.servlet.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.support.HttpStatus;

public class HttpResponse {

    private final HttpStatus status;
    private final String location;
    private final String contentType;
    private final String messageBody;

    private HttpResponse(HttpStatus status, String location, String contentType, String messageBody) {
        this.status = status;
        this.location = location;
        this.contentType = contentType;
        this.messageBody = messageBody;
    }

    public String toMessage() {
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
        if (location != null) {
            headers.add(String.format("Location: %s ", location));
        }
        if (contentType != null) {
            headers.add(String.format("Content-Type: %s ", contentType));
        }
        if (messageBody != null) {
            headers.add(String.format("Content-Length: %d ", messageBody.getBytes().length));
        }
        return headers;
    }

    public static class HttpResponseBuilder {

        final HttpStatus status;
        String location;
        String contentType;
        String messageBody;

        public HttpResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public HttpResponseBuilder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpResponseBuilder setLocation(String uri) {
            this.location = uri;
            return this;
        }

        public HttpResponseBuilder setMessageBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(status, location, contentType, messageBody);
        }
    }
}
