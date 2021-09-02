package nextstep.jwp.model.reponse;

import nextstep.jwp.model.request.Session;

public class Response {

    private static final String STATIC_FILE_MESSAGE_FORMAT = String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Content-Type: %s ",
            "Content-Length: %d ",
            "",
            "%s");
    private static final String STATIC_FILE_MESSAGE_FORMAT_WITH_COOKIE = String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Content-Type: %s ",
            "Content-Length: %d ",
            "Set-Cookie: %s",
            "",
            "%s");
    private static final String REDIRECT_MESSAGE_FORMAT = String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Location: %s ",
            "",
            "");

    private final String statusCode;
    private final String statusText;
    private final String contentType;
    private final String body;
    private final String location;
    private final Session session;
    private final int contentLength;
    private final boolean redirect;
    private final boolean setCookie;

    public static class Builder {

        private String statusCode;
        private String statusText;
        private String contentType;
        private String body;
        private String location;
        private Session session;
        private int contentLength;
        private boolean redirect;
        private boolean setCookie = true;

        public Builder() {
        }

        public Builder statusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder redirect(Boolean redirect) {
            this.redirect = redirect;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder setCookie(boolean setCookie) {
            this.setCookie = setCookie;
            return this;
        }

        public Builder session(Session session) {
            this.session = session;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    private Response(Builder builder) {
        this.statusCode = builder.statusCode;
        this.statusText = builder.statusText;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.body = builder.body;
        this.redirect = builder.redirect;
        this.location = builder.location;
        this.setCookie = builder.setCookie;
        this.session = builder.session;
    }

    public byte[] getBytes() {
        if (redirect) {
            return String.format(REDIRECT_MESSAGE_FORMAT,
                            statusCode,
                            statusText,
                            location)
                    .getBytes();
        }
        if (setCookie) {
            return String.format(STATIC_FILE_MESSAGE_FORMAT_WITH_COOKIE,
                            statusCode,
                            statusText,
                            contentType,
                            contentLength,
                            "JSESSIONID=" + session.getId(),
                            body)
                    .getBytes();
        }
        return String.format(STATIC_FILE_MESSAGE_FORMAT,
                        statusCode,
                        statusText,
                        contentType,
                        contentLength,
                        body)
                .getBytes();
    }
}
