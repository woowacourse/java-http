package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class StaticResourceController implements Controller {

    @Override
    public ResponseEntity run(final String startLin) {
        String path = startLin.split(" ")[1];
        return new ResponseEntity(StatusCode.OK, path);
    }
}
