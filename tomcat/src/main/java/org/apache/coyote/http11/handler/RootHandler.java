package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

public class RootHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) {
        String responseData = "Hello world!";
        return ResponseEntity.ok(responseData, "html");
    }
}
