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

    private HttpResponse(Builder builder) {
        this.startLine = builder.startLine;
        this.headers = builder.headers;
        this.responseBody = builder.responseBody;
    }

    public static class Builder {
        private ResponseLine startLine;
        private Map<String, String> headers = new LinkedHashMap<>();
        private String responseBody;

        public Builder(HttpStatus httpStatus, String responseBody, ContentType contentType) {
            this.startLine = new ResponseLine(httpStatus);
            this.responseBody = responseBody;
            initHeader(contentType, responseBody);
        }

        public Builder redirect(String redirectUrl) {
            headers.put("Location", redirectUrl);
            return this;
        }

        public Builder addJSessionId(Session session) {
            headers.put("Set-Cookie", "JSESSIONID=" + session.getId());
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

        private void initHeader(ContentType contentType, String responseBody) {
            headers.put("Content-Type", contentType.getContentType() + ";charset=utf-8");
            headers.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        }
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
