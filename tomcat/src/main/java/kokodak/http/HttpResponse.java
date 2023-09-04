package kokodak.http;

import static kokodak.Constants.BLANK;
import static kokodak.Constants.COLON;
import static kokodak.Constants.CRLF;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpVersion httpVersion;

    private HttpStatusCode httpStatusCode;

    private Map<String, String> header;

    private String body;

    private HttpResponse() {
    }

    private HttpResponse(final HttpResponseMessageBuilder builder) {
        this.httpVersion = builder.httpVersion;
        this.httpStatusCode = builder.httpStatusCode;
        this.header = builder.header;
        this.body = builder.body;
    }

    public String generateResponseMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        generateStatusLine(stringBuilder);
        generateHeader(stringBuilder);
        generateBody(stringBuilder);
        return stringBuilder.toString();
    }

    private void generateStatusLine(final StringBuilder stringBuilder) {
        stringBuilder.append(httpVersion.getValue())
                     .append(BLANK.getValue())
                     .append(httpStatusCode.getStatusCode())
                     .append(BLANK.getValue())
                     .append(httpStatusCode.getStatusMessage())
                     .append(BLANK.getValue())
                     .append(CRLF.getValue());
    }

    private void generateHeader(final StringBuilder stringBuilder) {
        header.forEach((key, value) -> stringBuilder.append(key)
                                                    .append(COLON.getValue())
                                                    .append(BLANK.getValue())
                                                    .append(value)
                                                    .append(BLANK.getValue())
                                                    .append(CRLF.getValue()));
        stringBuilder.append(CRLF.getValue());
    }

    private void generateBody(final StringBuilder stringBuilder) {
        stringBuilder.append(body);
    }

    public static HttpResponseMessageBuilder builder() {
        return new HttpResponseMessageBuilder();
    }

    public static class HttpResponseMessageBuilder {

        private HttpVersion httpVersion = HttpVersion.HTTP11;

        private HttpStatusCode httpStatusCode = HttpStatusCode.OK;

        private Map<String, String> header = new HashMap<>();

        private String body = "";

        private HttpResponseMessageBuilder() {
        }

        public HttpResponseMessageBuilder httpVersion(final HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public HttpResponseMessageBuilder httpStatusCode(final HttpStatusCode httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        public HttpResponseMessageBuilder header(final String key, final String value) {
            header.put(key, value);
            return this;
        }

        public HttpResponseMessageBuilder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponseMessageBuilder redirect(final String redirectUrl) {
            this.httpStatusCode = HttpStatusCode.FOUND;
            header.put("Location", redirectUrl);
            return this;
        }

        public HttpResponseMessageBuilder cookie(final String cookieValue) {
            header.put("Set-Cookie", cookieValue);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
