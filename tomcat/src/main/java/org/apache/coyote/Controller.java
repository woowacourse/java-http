package org.apache.coyote;

import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.controller.util.HttpResponse;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpSession;

import java.io.IOException;

public abstract class Controller {

    public abstract String run(final HttpRequest request) throws IOException;

    public String joinResponses(final String... responses) {
        return String.join(
                "\r\n",
                responses
        );
    }

    protected String createRedirectResponse(final FileResolver file) {
        return joinResponses(
                HttpResponse.HTTP_302_FOUND.getValue(),
                HttpResponse.getLocation(file),
                HttpResponse.EMPTY.getValue()
        );
    }

    protected String createRedirectResponseWithSession(final HttpSession session, final FileResolver file) {
        return joinResponses(
                HttpResponse.HTTP_302_FOUND.getValue(),
                HttpResponse.getLocation(file),
                HttpResponse.createSetSession(session),
                HttpResponse.EMPTY.getValue()
        );
    }
}
