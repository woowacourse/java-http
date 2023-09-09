package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootHandler implements Handler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        String responseData = "Hello world!";
        httpResponse.ok(responseData, ContentType.HTML);
    }
}
