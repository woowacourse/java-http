package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;

public interface Handler {

    ResponseEntity handle(HttpRequest request) throws IOException;
}
