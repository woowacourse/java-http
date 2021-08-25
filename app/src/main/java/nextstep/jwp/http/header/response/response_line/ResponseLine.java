package nextstep.jwp.http.header.response.response_line;

import nextstep.jwp.http.header.element.HttpStatus;
import nextstep.jwp.http.header.element.HttpVersion;

public class ResponseLine {
    private final HttpVersion version;
    private final HttpStatus statusCode;

    public ResponseLine(HttpVersion version, HttpStatus statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String asString() {
        return String.format("%s %s", version.asString(), statusCode.asString());
    }
}
