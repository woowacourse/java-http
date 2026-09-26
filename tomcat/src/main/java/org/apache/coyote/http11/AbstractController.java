package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {
    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        if (request.method().equals("GET")) {
            return doGet(request);
        }

        if (request.method().equals("POST")) {
            return doPost(request);
        }

        return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED, new byte[0]);
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED, new byte[0]);
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED, new byte[0]);
    }
}
