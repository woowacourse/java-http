package org.apache.coyote.http11;

public class ErrorController implements Controller{

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return HttpResponse.toNotFound();
    }
}
