package org.apache.coyote.controller;

import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController implements Controller {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();
        Map<String, String> queryParams = httpRequest.getQueryParams();
        Map<String, String> headers = httpRequest.getHttpHeaders();
        return new HttpResponse(headers.get("Accept"), DEFAULT_MESSAGE);
    }
}
