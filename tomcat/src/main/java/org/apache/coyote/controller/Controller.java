package org.apache.coyote.controller;

import org.apache.coyote.http11.DispatchResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    DispatchResult service(HttpRequest request, HttpResponse response) throws Exception;
}
