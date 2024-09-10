package org.apache.coyote.http11.error.errorhandler;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;

public class Error401Handler implements ErrorHandler {

    @Override
    public HttpResponse handle() {
        return new HttpResponse().statusCode(HttpStatusCode.OK_200)
                .viewUrl("/401.html");
    }
}
