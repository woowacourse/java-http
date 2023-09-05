package org.apache.coyote.http11.mvc;

import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    ResponseEntity handleRequest(HttpRequest request, HttpResponse response);
}
