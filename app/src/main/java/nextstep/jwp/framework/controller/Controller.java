package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;

public interface Controller {

    boolean canProcess(HttpRequest httpRequest);

    HttpResponse doService(HttpRequest httpRequest);
}
