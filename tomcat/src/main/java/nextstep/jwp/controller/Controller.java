package nextstep.jwp.controller;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface Controller {

    Response handle(Request request);
}
