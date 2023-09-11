package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class HelloController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        final String resourcePath = request.getRequestLine().getRequestUrl();
        final String responseBody = "Hello world!";

        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(ResourceResolver.resolve(resourcePath));
        response.addHeader("Content-Type", ContentType.from(resourcePath).getContentType() + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

}
