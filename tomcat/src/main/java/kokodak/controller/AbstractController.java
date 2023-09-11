package kokodak.controller;

import kokodak.http.HttpMethod;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        } else if (httpMethod == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception;

    protected abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception;
}
