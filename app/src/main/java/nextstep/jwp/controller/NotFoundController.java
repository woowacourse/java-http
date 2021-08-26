package nextstep.jwp.controller;

import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.IOException;

public class NotFoundController implements Controller {

    @Override
    public void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
    }

    @Override
    public boolean canForward() {
        return false;
    }

}
