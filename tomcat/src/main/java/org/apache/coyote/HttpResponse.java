package org.apache.coyote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public record HttpResponse(
        String version,
        StatusCode statusCode,
        Map<String, String> headers,
        String responseBody
) {
    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(200);
    }

    public static class HttpResponseBuilder {

        private final String version = "HTTP/1.1";
        private final StatusCode statusCode;
        private final Map<String, String> headers = new HashMap<>();

        private String responseBody;

        public HttpResponseBuilder(int statusCode) {
            this.statusCode = StatusCode.parse(statusCode);
        }

        public HttpResponseBuilder html(String data) {
            this.headers.put("Content-Type", "text/html;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.getBytes().length));
            this.responseBody = data;
            return this;
        }

        public HttpResponseBuilder css(String data) {
            this.headers.put("Content-Type", "text/css;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(data.getBytes().length));
            this.responseBody = data;
            return this;
        }

        public HttpResponseBuilder js(String data) {
            // TODO : 구현
            throw new UnsupportedOperationException();
        }

        public HttpResponse build() {
            return new HttpResponse(version, statusCode, headers, responseBody);
        }
    }

    private enum StatusCode {
        OK(200),
        ;

        final int codeNumber;

        StatusCode(int codeNumber) {
            this.codeNumber = codeNumber;
        }

        public static StatusCode parse(int codeNumber) {
            return Arrays.stream(StatusCode.values())
                    .filter(value -> value.codeNumber == codeNumber)
                    .findFirst()
                    .orElseThrow();
            // TODO : 명확한 예외 타입 사용
        }
    }

    public byte[] toByteArray() {
        return toSimpleString().getBytes();
    }

    public String toSimpleString() {
        StringJoiner result = new StringJoiner("\r\n");

        result.add("%s %s %s ".formatted(version, statusCode.codeNumber, statusCode.name()));
        headers.forEach((key, value) -> result.add("%s: %s ".formatted(key, value)));
        result.add("");
        result.add(responseBody);

        return result.toString();
    }
}
