package org.apache.coyote.http11.fixture;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.mvc.Controller;
import org.apache.mvc.annotation.RequestMapping;

public class TestController implements Controller {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity myMethod(HttpRequest httpRequest) {
        return new ResponseEntity(HttpStatus.OK, "hello");
    }
}
