package nextstep.jwp.http.response;

import java.util.Arrays;

public enum StatusMessage {
    OK("OK", 200), FOUND("Found", 302), NOT_FOUND("Not Found", 404), UNAUTHORIZED("Unauthorized", 401);

    private final String value;
    private final int statusCode;

    StatusMessage(String value, int statusCode) {
        this.value = value;
        this.statusCode = statusCode;
    }

    public static StatusMessage findByStatusCode(int statusCode) {
        return Arrays.stream(StatusMessage.values())
                .filter(message -> message.getStatusCode() == statusCode)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("상태코드 " + statusCode + "에 해당하는 상태 메세지가 없습니다."));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getValue() {
        return value;
    }
}
