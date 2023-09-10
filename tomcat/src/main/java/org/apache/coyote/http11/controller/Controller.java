package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

import java.io.IOException;

public interface Controller {

    ResponseEntity service(HttpRequest request) throws IOException;
}
