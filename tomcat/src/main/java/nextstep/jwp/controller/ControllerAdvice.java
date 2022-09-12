package nextstep.jwp.controller;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public class ControllerAdvice {

    public static HttpResponse handleUnauthorized() {
        return HttpResponseBuilder.found("/401");
    }

    public static HttpResponse handleNotFound() {
        return HttpResponseBuilder.found("/404");
    }
}
