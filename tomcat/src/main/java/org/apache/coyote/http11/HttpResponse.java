package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public record HttpResponse(String protocolVersion, int statusCode, String statusText,
                           Map<String, String> headers, Map<String, String> cookies, byte[] body) {

    public static Builder builder(Status status) {
        return new Builder()
                .protocolVersion("HTTP/1.1")
                .statusCode(status.getCode())
                .statusText(status.getMessage());
    }

    private static byte[] mergeByteArrays(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);

        return mergedArray;
    }

    public byte[] toMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(statusText).append(" ");
        headers.forEach((key, value) -> builder.append("\r\n").append(key).append(": ").append(value).append(" "));

        String cookiesMessage = cookies.entrySet().stream()
                .map(Entry::toString)
                .collect(Collectors.joining("; "));
        builder.append("\r\n").append("Set-Cookie: ").append(cookiesMessage).append(" ");

        if (body != null && body.length > 0) {
            builder.append("\r\n").append("Content-Length: ").append(body.length).append(" ");
            builder.append("\r\n\r\n");
            return mergeByteArrays(builder.toString().getBytes(), body);
        }

        return builder.toString().getBytes();
    }

    public static class Builder {
        private final Map<String, String> headers;
        private final Map<String, String> cookies;
        private String protocolVersion;
        private int statusCode;
        private String statusText;
        private byte[] body;

        private Builder() {
            headers = new HashMap<>();
            cookies = new HashMap<>();
        }

        public Builder protocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
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
            return new HttpResponse(protocolVersion, statusCode, statusText, headers, cookies, body);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "headers=" + headers +
                    ", cookies=" + cookies +
                    ", protocolVersion='" + protocolVersion + '\'' +
                    ", statusCode=" + statusCode +
                    ", statusText='" + statusText + '\'' +
                    ", body=" + Arrays.toString(body) +
                    '}';
        }
    }
}
