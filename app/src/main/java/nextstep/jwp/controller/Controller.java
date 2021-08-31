package nextstep.jwp.controller;

import java.io.FileNotFoundException;

import nextstep.jwp.handler.HttpRequest;
import nextstep.jwp.handler.HttpResponse;

public interface Controller {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException;
}
