package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        if (request.isSameMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        return doGet(request);
    }

    protected HttpResponse doPost(final HttpRequest request) {
        throw new UnsupportedHttpMethodException(request.getHttpMethod());
    }

    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        throw new UnsupportedHttpMethodException(request.getHttpMethod());
    }
}
