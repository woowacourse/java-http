package org.apache.coyote.http;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod.isGet()) {
            return doGet(httpRequest);
        }

        if (httpMethod.isPost()) {
            return doPost(httpRequest);
        }

        return HttpResponse.init(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBodyByPath("/500.html");
    }

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);
}
