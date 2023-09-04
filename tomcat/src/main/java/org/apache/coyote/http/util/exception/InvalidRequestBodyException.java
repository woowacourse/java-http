package org.apache.coyote.http.util.exception;

import java.io.IOException;

public class InvalidRequestBodyException extends IOException {

    public InvalidRequestBodyException() {
        super("요청 본문을 읽을 수 없습니다.");
    }
}
