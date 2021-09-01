package nextstep.jwp.service;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;

public interface Service {

    View doService(HttpRequest request, HttpResponse response);
}
