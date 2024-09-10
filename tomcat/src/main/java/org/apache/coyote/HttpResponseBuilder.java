package org.apache.coyote;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpResponseBuilder {
    public static HttpResponse build() {
        final HttpResponse response = new HttpResponse();
        response.setVersion("HTTP/1.1");
        response.setStatus(HttpStatusCode.OK);
        return response;
    }
    private HttpResponseBuilder() {}
}
