package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public interface Controller {

    ResponseEntity<? extends Object> handle(HttpRequest httpRequest);
}
