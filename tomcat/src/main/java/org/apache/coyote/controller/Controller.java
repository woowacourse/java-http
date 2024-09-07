package org.apache.coyote.controller;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse run(HttpRequest request, Manager manager);
}
