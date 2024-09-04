package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record Http11Response(String protocolVersion, int statusCode, String statusText,
                             Map<String, String> headers, byte[] body) {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(int statusCode) {
        if (statusCode == 200) {
            return new Builder()
                    .protocolVersion("HTTP/1.1")
                    .statusCode(statusCode)
                    .statusText("OK");
        }
        if (statusCode == 302) {
            return new Builder()
                    .protocolVersion("HTTP/1.1")
                    .statusCode(statusCode)
                    .statusText("Found");
        }
        if (statusCode == 404) {
            return new Builder()
                    .protocolVersion("HTTP/1.1")
                    .statusCode(statusCode)
                    .statusText("Not Found");
        }
        if (statusCode == 500) {
            return new Builder()
                    .protocolVersion("HTTP/1.1")
                    .statusCode(statusCode)
                    .statusText("Internal Server Error");
        }
        return builder();
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

        if (body != null && body.length > 0) {
            builder.append("\r\n").append("Content-Length: ").append(body.length).append(" ");
            builder.append("\r\n\r\n");
            return mergeByteArrays(builder.toString().getBytes(), body);
        }

        return builder.toString().getBytes();
    }

    public static class Builder {
        private final Map<String, String> headers;
        private String protocolVersion;
        private int statusCode;
        private String statusText;
        private byte[] body;

        private Builder() {
            headers = new HashMap<>();
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

        public Http11Response build() {
            return new Http11Response(protocolVersion, statusCode, statusText, headers, body);
        }

        @Override
        public String toString() {
            return "Http11ServletResponseBuilder{" +
                    "protocolVersion='" + protocolVersion + '\'' +
                    ", statusCode=" + statusCode +
                    ", statusText='" + statusText + '\'' +
                    ", headers=" + headers +
                    ", body=" + Arrays.toString(body) +
                    '}';
        }
    }
}
