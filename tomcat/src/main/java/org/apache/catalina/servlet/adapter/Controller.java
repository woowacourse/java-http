package org.apache.catalina.servlet.adapter;

import org.apache.coyote.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;

public interface Controller {
     ResponseEntity service(HttpRequest request);
}
