package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class HomeController implements Controller {

    @Override
    public ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody) {
        return new ResponseEntity(StatusCode.OK, httpHeader.getUrl()).body("Hello world!");
    }
}
