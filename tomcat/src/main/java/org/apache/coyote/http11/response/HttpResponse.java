package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ProtocolVersion;

public record HttpResponse(
        ProtocolVersion protocolVersion,
        Status status,
        Map<String, String> headers,
        Map<String, String> cookies,
        byte[] body
) {

    private static final String CRLF = "\r\n";
    private static final ProtocolVersion DEFAULT_PROTOCOL = ProtocolVersion.HTTP11;
    private static final String DELIMITER_COOKIE = "; ";
    private static final String DELIMITER_HEADER = ": ";
    private static final String DELIMITER_SPACE = " ";
    private static final String HEADER_NAME_SET_COOKIE = "Set-Cookie: ";
    private static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length: ";

    public static Builder builder() {
        return new Builder().protocolVersion(DEFAULT_PROTOCOL)
                .status(Status.OK);
    }

    private static byte[] mergeByteArrays(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);

        return mergedArray;
    }

    public byte[] toMessage() {
        StringBuilder builder = new StringBuilder();
        buildFirstLine(builder);
        buildHeaders(builder);
        buildCookies(builder);

        if (body != null && body.length > 0) {
            buildBody(builder);
            return mergeByteArrays(builder.toString().getBytes(), body);
        }
        return builder.toString().getBytes();
    }

    private void buildFirstLine(StringBuilder builder) {
        builder.append(protocolVersion.getValue()).append(DELIMITER_SPACE)
                .append(status.getCode()).append(DELIMITER_SPACE)
                .append(status.getMessage()).append(DELIMITER_SPACE);
    }

    private void buildHeaders(StringBuilder builder) {
        headers.forEach((key, value) -> builder.append(CRLF)
                .append(key).append(DELIMITER_HEADER)
                .append(value).append(DELIMITER_SPACE));
    }

    private void buildCookies(StringBuilder builder) {
        if (!cookies.isEmpty()) {
            String cookiesMessage = cookies.entrySet().stream()
                    .map(Entry::toString)
                    .collect(Collectors.joining(DELIMITER_COOKIE));
            builder.append(CRLF)
                    .append(HEADER_NAME_SET_COOKIE).append(cookiesMessage).append(DELIMITER_SPACE);
        }
    }

    private void buildBody(StringBuilder builder) {
        builder.append(CRLF).append(HEADER_NAME_CONTENT_LENGTH).append(body.length).append(DELIMITER_SPACE)
                .append(CRLF)
                .append(CRLF);
    }

    public static class Builder {
        private final Map<String, String> headers;
        private final Map<String, String> cookies;
        private ProtocolVersion protocolVersion;
        private Status status;
        private byte[] body;

        private Builder() {
            headers = new HashMap<>();
            cookies = new HashMap<>();
        }

        public Builder protocolVersion(ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder addCookie(String key, String value) {
            cookies.put(key, value);
            return this;
        }

        public Builder contentType(String value) {
            headers.put("Content-Type", value + ";charset=utf-8");
            return this;
        }

        public Builder location(String value) {
            headers.put("Location", value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(protocolVersion, status, headers, cookies, body);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "headers=" + headers +
                    ", cookies=" + cookies +
                    ", protocolVersion=" + protocolVersion +
                    ", status=" + status +
                    ", body=" + Arrays.toString(body) +
                    '}';
        }
    }
}
