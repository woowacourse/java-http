package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;

public interface Controller {

    View handle(HttpRequest request, HttpResponse response);
}
