package nextstep.jwp.framework.infrastructure.http.response;

import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;

public class HttpResponse {

    private static final String RESPONSE_FORMAT =
        String.join("\r\n",
            "%s %s %s ",
            "Content-Type: %s;charset=utf-8 ",
            "Content-Length: %d ", ""
        );

    private Protocol protocol;
    private HttpStatus httpStatus;
    private ContentType contentType;
    private String location;
    private int contentLength;
    private String responseBody;

    public HttpResponse(
        Protocol protocol,
        HttpStatus httpStatus,
        ContentType contentType,
        String location,
        int contentLength,
        String responseBody
    ) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.location = location;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }

    public static class Builder {

        private Protocol protocol;
        private HttpStatus httpStatus;
        private ContentType contentType;
        private String location;
        private String responseBody;
        private int contentLength;

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            this.contentLength = responseBody.getBytes().length;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                this.protocol,
                this.httpStatus,
                this.contentType,
                this.location,
                this.contentLength,
                this.responseBody
            );
        }
    }

    public String writeResponseMessage() {
        String header = String.format(RESPONSE_FORMAT,
            protocol.getName(),
            httpStatus.getCode(),
            httpStatus.getMessage(),
            contentType.getContentType(),
            contentLength
        );
        if (httpStatus.equals(HttpStatus.FOUND)) {
            header += "Location: " + location + "\r\n";
        }
        return header + "\r\n" + responseBody;
    }
}
