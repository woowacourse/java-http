package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class DashBoardController extends Controller {

    @Override
    public ResponseEntity run(final String startLin) throws IOException {
        String path = startLin.split(" ")[1];
        final String response = getContent(path);
        return new ResponseEntity(StatusCode.OK, path).body(response);
    }
}
