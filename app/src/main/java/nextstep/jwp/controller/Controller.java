package nextstep.jwp.controller;

import nextstep.jwp.model.httpMessage.request.HttpRequest;
import nextstep.jwp.model.httpMessage.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

}
