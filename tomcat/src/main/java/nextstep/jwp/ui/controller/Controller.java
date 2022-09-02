package nextstep.jwp.ui.controller;

import http.HttpRequest;

public interface Controller {

    boolean support(String requestUri);

    String service(final HttpRequest httpRequest);
}
