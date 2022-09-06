package org.apache.coyote.http11.handler;

import java.io.IOException;

import nextstep.jwp.presentation.Controller;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.ResponseEntity;

public class RequestHandler {

    public static ResponseEntity handle(
            final Controller controller,
            final HttpHeader httpHeader,
            final HttpBody httpBody) throws IOException
    {
        return controller.run(httpHeader, httpBody);
    }
}
