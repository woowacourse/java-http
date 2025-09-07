package com.techcourse;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class Controller {

    public HttpResponse hello() {
        return ResponseEntity.ok("hello world!");
    }
}
