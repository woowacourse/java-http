package nextstep.jwp.controller;


import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

import java.util.UUID;

import static nextstep.jwp.http.HttpCookie.JSESSIONID;

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
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void setJSessionId(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpCookie httpCookie = httpRequest.getCookies();
        if (httpCookie.getCookie(JSESSIONID) != null) {
            httpResponse.addHeader("Set-Cookie", JSESSIONID +"="+httpCookie.getCookie(JSESSIONID));
            return;
        }

        httpResponse.addHeader("Set-Cookie", JSESSIONID +"="+ UUID.randomUUID());
    }
}
