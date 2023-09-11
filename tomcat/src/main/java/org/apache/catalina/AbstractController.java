package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException();
    }


    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
    }
}
