package org.apache.coyote.http11.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public interface Controller {

    ResponseEntity handleRequest(HttpRequest request, HttpResponse response);
}
