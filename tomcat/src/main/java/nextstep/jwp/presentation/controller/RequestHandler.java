package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public interface RequestHandler {
    String handle(Http11Request request, Http11Response response);

    boolean support(Http11Request request);
}
