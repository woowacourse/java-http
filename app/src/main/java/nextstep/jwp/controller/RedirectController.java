package nextstep.jwp.controller;

import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class RedirectController implements Controller {
    @Override
    public void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        httpResponseMessage.setStatusCode(HttpStatusCode.FOUND);
        httpResponseMessage.putHeader("Location", "/index.html");
    }
}
