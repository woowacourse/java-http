package nextstep.jwp.http.controller.stationary;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class RedirectController implements Controller {
    @Override
    public void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        httpResponseMessage.setStatusCode(HttpStatusCode.FOUND);
        httpResponseMessage.putHeader("Location", "/index.html");
    }
}
