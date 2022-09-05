package nextstep.jwp.presentation;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class HomeController implements Controller {

    @Override
    public ResponseEntity run(final String startLin) {
        String path = startLin.split(" ")[1];
        return new ResponseEntity(StatusCode.OK, path).body("Hello world!");
    }
}
