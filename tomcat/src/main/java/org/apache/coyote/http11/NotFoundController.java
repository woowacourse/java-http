package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class NotFoundController implements Controller{

    @Override
    public HttpResponse service(HttpRequest request) {
        final String responseBody = ResourceFindUtils.getResourceFile("/404.html");
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .contentType("text/html")
                .location("/404.html")
                .responseBody(responseBody)
                .build();
    }
}
