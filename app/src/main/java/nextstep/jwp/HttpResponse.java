package nextstep.jwp;

public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final String body;

    public static class Builder {
        private int statusCode = 200;
        private String statusMessage = "OK";
        private String body = "";

        public Builder() {
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    private HttpResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.body = builder.body;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
            "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + body.getBytes().length + " ",
            "",
            body);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
