package org.apache.coyote.http11.error.errorhandler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBuilder;

public class Error401Handler implements ErrorHandler {

    @Override
    public HttpResponse handleError() {
        return new ResponseBuilder()
                .statusCode(200)
                .viewUrl("/401.html")
                .build();
    }
}
