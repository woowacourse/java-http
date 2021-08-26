package nextstep.jwp.controller;

import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.IOException;

public interface Controller {
    void service(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException;
}
