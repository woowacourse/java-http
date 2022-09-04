package org.apache.coyote.http11.message.response;

import static org.apache.coyote.http11.message.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.common.HttpHeader.CONTENT_TYPE;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.DuplicateHeaderException;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.common.HttpVersion;

public class HttpResponse {

    private static final String RESPONSE_LINE_FORMAT = "%s %s %s ";

    private static final String NEW_LINE = "\r\n";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String body;

    private HttpResponse(final Builder builder) {
        this.httpVersion = builder.httpVersion;
        this.httpStatus = builder.httpStatus;
        this.httpHeaders = new HttpHeaders(builder.headers);
        this.body = builder.body;
    }

    public String generateMessage() {
        return generateResponseLine() + NEW_LINE
                + httpHeaders.generateHeaderText() + NEW_LINE
                + body;
    }

    private String generateResponseLine() {
        return String.format(RESPONSE_LINE_FORMAT, httpVersion.getName(), httpStatus.getCode(), httpStatus.name());
    }

    public static class Builder {

        private final Map<String, String> headers;
        private HttpVersion httpVersion;
        private HttpStatus httpStatus;
        private String body;

        public Builder() {
            this.httpVersion = HttpVersion.HTTP11;
            this.httpStatus = HttpStatus.OK;
            this.headers = new LinkedHashMap<>();
            this.body = "";
        }

        public Builder version(final HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder header(final String key, final String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder status(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(final ContentType contentType) {
            validateDuplicateHeader();
            this.headers.put(CONTENT_TYPE, contentType.getValue() + ";charset=utf-8");
            return this;
        }

        public Builder contentType(final String fileExtension) {
            validateDuplicateHeader();
            this.headers.put(CONTENT_TYPE, ContentType.from(fileExtension).getValue() + ";charset=utf-8");
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            this.headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

        private void validateDuplicateHeader() {
            if (this.headers.containsKey(CONTENT_TYPE)) {
                throw new DuplicateHeaderException();
            }
        }

    }
}
