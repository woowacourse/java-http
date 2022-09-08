package org.apache.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
