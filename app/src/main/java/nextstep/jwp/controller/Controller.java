package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

}
