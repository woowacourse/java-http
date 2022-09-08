package org.apache.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request);
}
