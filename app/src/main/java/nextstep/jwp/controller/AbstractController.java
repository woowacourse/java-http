package nextstep.jwp.controller;


import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
public class AbstractController implements Controller{

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.checkMethod("GET")) {
            doGet(httpRequest, httpResponse);
        }

        if (httpRequest.checkMethod("POST")) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }
}
