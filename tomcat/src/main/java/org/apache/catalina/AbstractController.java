package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            return doPost(request);
        }
        throw new UnsupportedOperationException();
    }


    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return null;
    }

    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return null;
    }
}
