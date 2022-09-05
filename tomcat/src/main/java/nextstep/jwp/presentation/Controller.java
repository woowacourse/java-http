package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.ResponseEntity;

public interface Controller {

    ResponseEntity run(final String startLin);
}
