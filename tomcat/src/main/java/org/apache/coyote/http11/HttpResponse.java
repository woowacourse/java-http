package org.apache.coyote.http11;

public class HttpResponse {
    private String statusCode;
    private String statusText;
    private String contentType;
    private String responseBody;
    private String location;
    private String jSessionId;

    public HttpResponse() {
    }

    public HttpResponse(String statusCode, String statusText, String contentType, String responseBody, String location, String jSessionId) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
        this.jSessionId = jSessionId;
    }

    public HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    class HttpResponseBuilder {
        private String statusCode;
        private String statusText;
        private String contentType;
        private String responseBody;
        private String location;
        private String jSessionId;

        public HttpResponseBuilder statusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HttpResponseBuilder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public HttpResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
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

        public HttpResponseBuilder jSessionId(String jSessionId) {
            this.jSessionId = jSessionId;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, statusText, contentType, responseBody, location, jSessionId);
        }
    }

    public static HttpResponse of(HttpRequests httpRequests, String responseBody) {
        return new HttpResponse().builder()
                .statusCode(httpRequests.getStatusCode())
                .statusText(httpRequests.getStatusText())
                .contentType(httpRequests.getContentType())
                .responseBody(responseBody)
                .build();
    }

    public static HttpResponse ofLoginRedirect(HttpRequests httpRequests, String jSessionId) {
        return new HttpResponse().builder()
                .statusCode(httpRequests.getStatusCode())
                .statusText(httpRequests.getStatusText())
                .location(httpRequests.getFileName())
                .jSessionId(jSessionId)
                .build();
    }

    public String serialize() {
        if (location != null) {
            return String.join("\r\n",
                    "HTTP/1.1 " + this.statusCode + " " + this.statusText + " ",
                    "Location: " + this.location + " ",
                    "Set-Cookie: JSESSIONID=" + this.jSessionId + " ",
                    "");
        }
        return String.join("\r\n",
                "HTTP/1.1 " + this.statusCode + " " + this.statusText + " ",
                "Content-Type: " + this.contentType + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
