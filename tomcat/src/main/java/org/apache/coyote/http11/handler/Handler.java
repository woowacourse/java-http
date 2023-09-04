package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResponseEntity;

import java.io.IOException;

public interface Handler {

    ResponseEntity handle(HttpRequest request) throws IOException;
}
