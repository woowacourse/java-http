package nextstep.jwp.http.controller;

import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.IOException;

public interface Controller {
    HttpResponseMessage service(HttpRequestMessage httpRequestMessage);
}
