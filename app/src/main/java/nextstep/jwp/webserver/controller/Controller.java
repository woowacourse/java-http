package nextstep.jwp.webserver.controller;

import java.io.IOException;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest) throws IOException;
}
