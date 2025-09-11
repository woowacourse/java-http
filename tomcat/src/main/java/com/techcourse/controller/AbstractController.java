package com.techcourse.controller;

import com.techcourse.util.ResourceParser;
import java.io.IOException;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpRequest.RequestMethod;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        final RequestMethod requestMethod = httpRequest.getRequestMethod();

        if (requestMethod == RequestMethod.GET) {
            return doGet(httpRequest);
        }

        if (requestMethod == RequestMethod.POST) {
            return doPost(httpRequest);
        }

        return defaultResponse();
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return defaultResponse();
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        return defaultResponse();
    }

    private HttpResponse defaultResponse() throws IOException {
        return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED)
                .build(Page.METHOD_NOT_ALLOWED.getPath(), ResourceParser.parse(Page.METHOD_NOT_ALLOWED.getPath()));
    }
}
