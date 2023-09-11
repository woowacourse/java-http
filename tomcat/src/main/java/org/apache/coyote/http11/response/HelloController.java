package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class HelloController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        final String responseBody = "Hello world!";

        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(responseBody);
        response.addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

}
