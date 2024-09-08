package com.techcourse.controller.model;

import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.response.model.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);

    HttpResponse service(HttpRequest httpRequest);

    HttpResponse doPost(HttpRequest httpRequest);

    HttpResponse doGet(HttpRequest httpRequest);
}
