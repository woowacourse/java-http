package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public record HttpResponse(
        String version,
        int statusCode,
        Map<String, String> headers,
        String responseBody
) {
    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(200);
    }

    public static class HttpResponseBuilder {

        private final String version = "HTTP/1.1";
        private final int statusCode;
        private final Map<String, String> headers = new HashMap<>();

        private String responseBody;

        public HttpResponseBuilder(int statusCode) {
            this.statusCode = statusCode;
        }

        public HttpResponseBuilder html(String pageData) {
            this.headers.put("Content-Type", "text/html;charset=utf-8");
            this.headers.put("Content-Length", String.valueOf(pageData.getBytes().length));
            this.responseBody = pageData;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(version, statusCode, headers, responseBody);
        }
    }

    /*
    return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
     */

    public byte[] toByteArray() {
        return toSimpleString().getBytes();
    }

    public String toSimpleString() {
        // TODO : 구현
        throw new UnsupportedOperationException();
    }
}
