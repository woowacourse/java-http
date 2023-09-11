package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws IOException;

    class DefaultController extends AbstractController {

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
            setResponse(response, "/500", HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
            final String resource = request.getRequestPath().getResource();

            setResponse(response, resource, HttpStatusCode.OK);
        }
    }
}
