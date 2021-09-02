package nextstep.jwp.model.response;

import nextstep.jwp.model.ProtocolType;
import nextstep.jwp.model.request.Session;

public class Response {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public static class Builder {

        private StatusLine statusLine;
        private ResponseHeaders responseHeaders = new ResponseHeaders();
        private ResponseBody responseBody;

        public Builder() {
        }

        public Builder statusLine(ProtocolType protocol, StatusType status) {
            statusLine = new StatusLine(protocol, status);
            return this;
        }

        public Builder contentType(String contentType) {
            responseHeaders.putContentType(contentType);
            return this;
        }

        public Builder contentLength(int contentLength) {
            responseHeaders.putContentLength(contentLength);
            return this;
        }

        public Builder location(String location) {
            responseHeaders.putLocation(location);
            return this;
        }

        public Builder setCookie(boolean setCookie, Session session) {
            if (setCookie) {
                responseHeaders.putSetCookie(session.getId());
            }
            return this;
        }

        public Builder body(String body) {
            responseBody = new ResponseBody(body);
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    private Response(Builder builder) {
        this.statusLine = builder.statusLine;
        this.responseHeaders = builder.responseHeaders;
        this.responseBody = builder.responseBody;
    }

    public byte[] getBytes() {
        String response = statusLine.toString() + "\r\n"
                + responseHeaders.toString() + "\r\n"
                + responseBody.toString();
        return response.getBytes();
    }
}
