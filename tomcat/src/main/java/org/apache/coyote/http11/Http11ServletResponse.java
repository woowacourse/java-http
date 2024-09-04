package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record Http11ServletResponse(String protocolVersion, int statusCode, String statusText,
                                    Map<String, String> headers, byte[] body) {

    public static Http11ServletResponse.Http11ServletResponseBuilder builder() {
        return new Http11ServletResponse.Http11ServletResponseBuilder();
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
