package nextstep.jwp.controller;

import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

}
