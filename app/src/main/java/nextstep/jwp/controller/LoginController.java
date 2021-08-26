package nextstep.jwp.controller;

import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class LoginController extends AbstractController {
    @Override
    public void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        httpRequestMessage.changeRequestUri("/login.html");
    }

    @Override
    public boolean canForward() {
        return true;
    }
}
