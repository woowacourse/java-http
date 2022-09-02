package nextstep.jwp.ui;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.mvc.Controller;
import org.apache.mvc.annotation.RequestMapping;

public class DashboardController implements Controller {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity handleRoot(HttpRequest request) {
        return new ResponseEntity(HttpStatus.OK, "hello root");
    }

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public ResponseEntity handleIndex(HttpRequest request) {
        return new ResponseEntity(HttpStatus.OK, "hello index");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity handleLogin(HttpRequest request) {
        return new ResponseEntity(HttpStatus.OK, "hello login");
    }
}
