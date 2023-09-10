package org.apache.front;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class StaticMapping implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setFileAsBody(httpRequest.getRequestUrl(), httpRequest.getContentType());
    }
}
