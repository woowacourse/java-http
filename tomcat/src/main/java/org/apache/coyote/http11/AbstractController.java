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

        return HttpResponse.of("405 Method Not Allowed", new byte[0]);
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return HttpResponse.of("405 Method Not Allowed", new byte[0]);
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return HttpResponse.of("405 Method Not Allowed", new byte[0]);
    }
}
