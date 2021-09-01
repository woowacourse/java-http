package nextstep.jwp.controller;

import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.response.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws Exception;
}
