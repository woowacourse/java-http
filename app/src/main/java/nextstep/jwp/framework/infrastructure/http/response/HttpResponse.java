package nextstep.jwp.framework.infrastructure.http.response;

import java.util.EnumMap;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpResponse {

    private static final String HTTP_RESPONSE_FORMAT = "%s\r\n%s";
    private static final String COOKE_FORMAT = "%s=%s";

    private final HttpResponseHeader httpResponseHeader;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(
        HttpResponseHeader httpResponseHeader,
        HttpResponseBody httpResponseBody
    ) {
        this.httpResponseHeader = httpResponseHeader;
        this.httpResponseBody = httpResponseBody;
    }

    public static class Builder {

        private Protocol protocol;
        private HttpStatus httpStatus;
        private String responseBody;
        private final OtherResponseLines otherResponseLines =
            new OtherResponseLines(new EnumMap<>(HttpHeaders.class));

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            otherResponseLines.add(HttpHeaders.CONTENT_TYPE, contentType.getContentType());
            return this;
        }

        public Builder location(String location) {
            otherResponseLines.add(HttpHeaders.LOCATION, location);
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            int contentLength = responseBody.getBytes().length;
            otherResponseLines.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
            return this;
        }

        public Builder cookie(String key, String value) {
            String cookie = String.format(COOKE_FORMAT, key, value);
            otherResponseLines.add(HttpHeaders.SET_COOKIE, cookie);
            return this;
        }

        public HttpResponse build() {
            ResponseLine responseLine = new ResponseLine(protocol, httpStatus);
            HttpResponseHeader httpResponseHeader =
                new HttpResponseHeader(responseLine, otherResponseLines);
            HttpResponseBody httpResponseBody = new HttpResponseBody(responseBody);
            return new HttpResponse(httpResponseHeader, httpResponseBody);
        }
    }

    public String writeResponseMessage() {
        String header = httpResponseHeader.write();
        String body = httpResponseBody.getResponseBody();
        return String.format(HTTP_RESPONSE_FORMAT, header, body);
    }
}
