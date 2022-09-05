package org.apache.coyote.http11;

public class HttpResponse {

    private final String value;

    public HttpResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContentType contentType;
        private String responseBody;

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                    String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: " + contentType.getMimeType() + ";charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody)
            );
        }
    }

}
