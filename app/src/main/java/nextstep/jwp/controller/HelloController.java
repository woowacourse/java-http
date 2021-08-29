package nextstep.jwp.controller;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        MessageBody messageBody = new MessageBody("Hello world!");
        httpResponseMessage.setMessageBody(messageBody);

        httpResponseMessage.setStatusCode(HttpStatusCode.OK);
        httpResponseMessage.putHeader("Content-Type", "text/html;charset=utf-8");
        httpResponseMessage.putHeader("Content-Length", messageBody.contentLength());
    }
}
