package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record Http11ServletResponse(String protocolVersion, int statusCode, String statusText,
                                    Map<String, String> headers, byte[] body) {

    public static Http11ServletResponse.Http11ServletResponseBuilder builder() {
        return new Http11ServletResponse.Http11ServletResponseBuilder();
    }

    public static byte[] mergeByteArrays(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);

        return mergedArray;
    }

    public byte[] toMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(statusText).append("\r\n")
                .append(headers.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()))
                .append("\r\n");

        if (body != null && body.length > 0) {
            builder.append("\r\n");
            return mergeByteArrays(builder.toString().getBytes(), body);
        }

        return builder.toString().getBytes();
    }

    public static class Http11ServletResponseBuilder {
        private String protocolVersion;
        private int statusCode;
        private String statusText;
        private Map<String, String> headers;
        private byte[] body;

        Http11ServletResponseBuilder() {
            headers = new HashMap<>();
        }

        public Http11ServletResponse.Http11ServletResponseBuilder protocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Http11ServletResponse.Http11ServletResponseBuilder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Http11ServletResponse.Http11ServletResponseBuilder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Http11ServletResponse.Http11ServletResponseBuilder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Http11ServletResponse.Http11ServletResponseBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Http11ServletResponse build() {
            return new Http11ServletResponse(protocolVersion, statusCode, statusText, headers, body);
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
