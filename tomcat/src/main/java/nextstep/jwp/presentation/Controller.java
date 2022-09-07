package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseEntity;

public interface Controller {

    ResponseEntity service(final HttpRequest httpRequest,
                           final HttpResponse httpResponse);
}
