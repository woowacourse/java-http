package nextstep.jwp.controller;

import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;

public interface Controller {

    void service(Request request, Response response);
}
