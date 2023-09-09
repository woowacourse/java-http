package nextstep.jwp.controller.dto;

import org.apache.coyote.http11.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Response {
    private final HttpStatus httpStatus;
    private final Map<String, String> responseHeader = new HashMap<>();

    public Response(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String get(final String key) {
        return responseHeader.get(key);
    }

    public void setCookie(final UUID session) {
        responseHeader.put("Set-Cookie", "JSESSIONID=" + session);
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
