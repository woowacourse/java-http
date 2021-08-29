package nextstep.jwp.dispatcher.handler;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.HttpMethod;

public abstract class HttpHandler implements Handler {

    protected HttpHandler() {
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpMethod method = httpRequest.getMethod();
        if (method == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        } else if (method == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
    }
}
