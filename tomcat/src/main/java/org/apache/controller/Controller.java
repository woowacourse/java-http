package org.apache.controller;

import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException;

}
