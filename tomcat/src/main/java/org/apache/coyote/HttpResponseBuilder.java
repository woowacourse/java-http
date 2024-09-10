package org.apache.coyote;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.version.HttpVersion;

public class HttpResponseBuilder {
    public static HttpResponse build() {
        final HttpResponse response = new HttpResponse();
        response.setVersion(HttpVersion.HTTP_1_1);
        response.setStatus(HttpStatusCode.OK);
        return response;
    }
    private HttpResponseBuilder() {}
}
