package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.HttpResponse;

public class Http11Response implements HttpResponse {

    private final String protocol;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String body;

    private Http11Response(Http11ResponseBuilder builder) {
        this.protocol = builder.protocol;
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public static Http11ResponseBuilder builder() {
        return new Http11ResponseBuilder();
    }

    @Override
    public String getResponseMessage() {
        String headerMessage = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                protocol + " " + statusCode + " " + statusMessage + " ",
                headerMessage,
                "",
                body
        );
    }

    public static class Http11ResponseBuilder {
        private String protocol;
        private int statusCode;
        private String statusMessage;
        private Map<String, String> headers;
        private String body;

        private Http11ResponseBuilder() {
            this.protocol = "HTTP/1.1";
            this.headers = new LinkedHashMap<>();
            this.body = "";
        }

        public Http11ResponseBuilder status(HttpStatus status) {
            this.statusCode = status.statusCode();
            this.statusMessage = status.statusMessage();
            return this;
        }

        public Http11ResponseBuilder appendHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Http11ResponseBuilder body(String body) {
            this.body = body;
            appendHeader("Content-Length", body.getBytes().length + " ");
            return this;
        }

        public Http11Response build() {
            return new Http11Response(this);
        }
    }
}
