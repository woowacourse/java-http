package nextstep.jwp.presenstation;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.http.HttpRequest;

public interface RequestHandler {

    ResponseEntity handle(HttpRequest httpRequest);
}
