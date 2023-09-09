package nextstep.jwp.http.common;

import nextstep.jwp.exception.BadRequestException;

public class HttpBody {

    private String body;

    private HttpBody(String body) {
        this.body = body;
    }

    public static HttpBody from(String message) {
        if (message == null) {
            throw new BadRequestException("HttpBody is Null");
        }

        return new HttpBody(message);
    }

    public static HttpBody createEmptyHttpBody() {
        return new HttpBody("");
    }

    public void clear() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getBytesLength() {
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }

}
