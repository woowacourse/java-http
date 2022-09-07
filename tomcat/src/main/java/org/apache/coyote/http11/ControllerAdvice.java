package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.response.HttpResponse;

public class ControllerAdvice {

    public static HttpResponse handleUnauthorized() throws IOException {
        return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/401");
    }

    public static HttpResponse handleNotFound() throws IOException {
        return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/404");
    }
}
