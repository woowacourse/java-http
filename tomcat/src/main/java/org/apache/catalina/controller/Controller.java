package org.apache.catalina.controller;

import org.apache.catalina.DispatchResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    DispatchResult service(HttpRequest request, HttpResponse response) throws Exception;
}
