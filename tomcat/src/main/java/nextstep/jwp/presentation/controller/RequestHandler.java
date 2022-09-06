package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public interface RequestHandler {
    String handle(Request request, Response response);

    boolean support(Request request);
}
