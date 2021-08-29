package nextstep.jwp.exception;

import nextstep.jwp.http.Request;

public class NotFoundException extends RuntimeException {

    private final String message;

    public NotFoundException(Request request) {
        this.message = String.join(" ",
            request.getMethod().toString(),
            request.getUri(),
            request.getHttpVersion(),
            "{} {} {} 에 대한 정보를 찾을 수 없습니다."
            );
    }

    @Override
    public String getMessage() {
        return message;
    }
}
