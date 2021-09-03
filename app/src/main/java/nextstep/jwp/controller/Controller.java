package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.response.Response;

public interface Controller {

    Response doService(Request request) throws IOException;
}
