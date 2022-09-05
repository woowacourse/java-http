package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;

public interface Controller {

    Response execute(Request request);
}
