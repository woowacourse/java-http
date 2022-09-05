package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.response.HttpResponse1;

public class ControllerAdvice {

    public static HttpResponse1 getLoginExceptionResponse() throws IOException {
        return HttpResponse1.from(HttpStatus.FOUND, "/401");
    }
}
