package org.apache.coyote.http11;

import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseBuilder;

public class HttpResponseCreator {

    public HttpResponse okResponse(final String contentType, final String responseBody) {
        return new HttpResponseBuilder()
                .version()
                .status("OK")
                .contentType(contentType)
                .contentLength(responseBody.getBytes().length)
                .responseBody(responseBody)
                .build();
    }
}
