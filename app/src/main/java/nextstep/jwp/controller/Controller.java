package nextstep.jwp.controller;

import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws Exception;
}
