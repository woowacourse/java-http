package org.apache.coyote.common.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.header.Header;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;

public class Response {

    private static final String RESPONSE_MESSAGE_DELIMITER = "\r\n";

    private final String startLine;
    private final String headers;
    private final String body;

    public static ResponseBuilder builder(final HttpVersion httpVersion, final Status status) {
        return new ResponseBuilder(httpVersion, status);
    }

    private Response(final String startLine, final String headers, final String body) {
        this.startLine = startLine + " ";
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        return String.join(RESPONSE_MESSAGE_DELIMITER,
                startLine,
                headers,
                body);
    }

    public static class ResponseBuilder {

        private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
        private static final String HEADER_BODY_DELIMITER = "";
        private static final String HEADER_VALUE_DELIMITER = ";";

        private final String startLine;
        private final Map<String, String> headers;
        private String body;

        ResponseBuilder(final HttpVersion httpVersion, final Status status) {
            this.startLine = String.join(" ", httpVersion.getValue(), status.getValue());
            this.headers = new HashMap<>();
            headers.put(Header.CONTENT_TYPE.getValue(), MediaType.TEXT_HTML.getValue());
            this.body = "";
        }

        public ResponseBuilder setContentType(final MediaType mediaType, final Charset charset) {
            return setHeader(Header.CONTENT_TYPE,
                    combineHeaderValues(mediaType.getValue(), "charset=" + charset.getValue()));
        }

        public ResponseBuilder setLocation(final String url) {
            return setHeader(Header.LOCATION, url);
        }

        private String combineHeaderValues(final String... values) {
            return String.join(HEADER_VALUE_DELIMITER, values);
        }

        public ResponseBuilder setContentType(final MediaType mediaType) {
            return setContentType(mediaType, Charset.UTF8);
        }

        public ResponseBuilder setContentLength(final int length) {
            return setHeader(Header.CONTENT_LENGTH, length + "");
        }

        public ResponseBuilder setHeader(final Header header, final String headerValue) {
            headers.put(header.getValue(), headerValue);
            return this;
        }

        public ResponseBuilder setBody(final String body) {
            this.body = body;
            return this;
        }

        public Response build() {
            return new Response(this.startLine, combineHeaders(), this.body);
        }

        private String combineHeaders() {
            return headers.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + HEADER_KEY_VALUE_DELIMITER + entry.getValue() + " ")
                    .collect(Collectors.joining(RESPONSE_MESSAGE_DELIMITER, "",
                            HEADER_BODY_DELIMITER + RESPONSE_MESSAGE_DELIMITER));
        }
    }
}
