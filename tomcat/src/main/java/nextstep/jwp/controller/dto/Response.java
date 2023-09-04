package nextstep.jwp.controller.dto;

import org.apache.coyote.http11.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Response {
    private final Map<String, String> responseHeader = new HashMap<>();
    private final HttpStatus httpStatus;

    public Response(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
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
