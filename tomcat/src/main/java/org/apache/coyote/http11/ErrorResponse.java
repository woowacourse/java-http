package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ErrorResponse {

    public static HttpResponse resourceNotFound(){
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location("/404.html")
                .build();
    }

    public static HttpResponse loginFailed() {
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location("/401.html")
                .build();
    }
}
