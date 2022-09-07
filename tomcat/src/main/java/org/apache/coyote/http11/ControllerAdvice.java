package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.response.HttpResponse;

public class ControllerAdvice {

    public static HttpResponse getLoginExceptionResponse() throws IOException {
        return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/401");
    }
}
