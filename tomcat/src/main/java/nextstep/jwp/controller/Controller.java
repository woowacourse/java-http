package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;

public interface Controller {

    void service(Request request, Response response);
}
