package org.apache.coyote.http11;

public abstract class Controller {

    public final HttpResponse doService(HttpRequest request) {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return doGet(request);
        }

        if (request.getHttpMethod() == HttpMethod.POST) {
            return doPost(request);
        }

        return HttpResponse.notFound().build();
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.notFound().build();
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return HttpResponse.notFound().build();
    }
}
