package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        switch (request.method()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
        }
    }

    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
    }

    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
    }
}
