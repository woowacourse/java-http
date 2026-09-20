package org.apache.coyote.http11.response.line;

import org.apache.coyote.http11.HttpVersion;

/**
 * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Guides/Messages#http_%EC%9D%91%EB%8B%B5"> MDN HTTP 응답 상태라인 </a>
 * */

public class StatusLine {

    private static final String DELIMITER = " ";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine from(HttpStatus httpStatus) {
        return new StatusLine(HttpVersion.HTTP_1_1, httpStatus);
    }

    public String serialize() {
        return String.join(DELIMITER,
                httpVersion.getVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getMessage());
    }

}
