package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

public interface Controller {
    HttpResponseMessage service(HttpRequestMessage httpRequestMessage);
}
