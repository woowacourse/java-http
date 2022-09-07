package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.ResponseEntity;

public interface Controller {

    ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody);
}
