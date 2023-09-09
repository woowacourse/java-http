package org.apache.catalina.servlet.adapter;

import org.apache.coyote.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;

public interface Handler {
     ResponseEntity service(HttpRequest request);
}
