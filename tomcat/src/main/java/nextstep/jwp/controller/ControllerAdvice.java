package nextstep.jwp.controller;

import static org.apache.coyote.http11.HeaderField.LOCATION;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;

public class ControllerAdvice {

    public static HttpResponse handleUnauthorized() {
        ResponseBody body = new ResponseBody();
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(LOCATION, "/404");
        return HttpResponse.create(HttpStatus.FOUND, headers, body);
    }

    public static HttpResponse handleNotFound() {
        ResponseBody body = new ResponseBody();
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(LOCATION, "/401");
        return HttpResponse.create(HttpStatus.FOUND, headers, body);
    }
}
