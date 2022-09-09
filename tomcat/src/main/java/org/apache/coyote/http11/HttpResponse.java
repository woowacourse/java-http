package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private HttpStatus status;
    private HttpCookie cookie;
    private String mimeType;
    private String responseBody;
    private String location;

    public HttpResponse(HttpStatus status, HttpCookie cookie, String mimeType, String responseBody,
                        String location) {
        this.status = status;
        this.cookie = cookie;
        this.mimeType = mimeType;
        this.responseBody = responseBody;
        this.location = location;
    }

    public static class HttpResponseBuilder {
        private HttpStatus status;
        private HttpCookie cookie;
        private String mimeType;
        private String responseBody;
        private String location;

        public HttpResponseBuilder() {
        }

        public HttpResponseBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public HttpResponseBuilder cookie(HttpCookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public HttpResponseBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public HttpResponseBuilder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public String toResponse() {
            if (!cookie.isExistCookie()) {
                return String.join("\r\n",
                        "HTTP/1.1 " + status.getStatusCode() + " ",
                        "Content-Type: " + mimeType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            return String.join("\r\n",
                    "HTTP/1.1 " + status.getStatusCode() + " ",
                    toCookieHeader(),
                    "Content-Type: " + mimeType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        public String toRedirectResponse() {
            List<String> response = new LinkedList<>();
            response.add("HTTP/1.1 " + status.getStatusCode() + " ");
            if (cookie.isExistCookie()) {
                response.add(toCookieHeader());
            }
            response.add("Location: " + location + " ");
            return response.stream()
                    .collect(Collectors.joining("\r\n"));
        }

        private String toCookieHeader() {
            List<String> result = new ArrayList<>();
            Map<String, String> cookies = cookie.getCookies();
            for (String key : cookies.keySet()) {
                String value = cookies.get(key);
                String header = "Set-Cookie: " + key + "=" + value + " ";
                result.add(header);
            }
            return String.join("\r\n", result);
        }
    }
}
