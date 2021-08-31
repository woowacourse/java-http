package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public interface Controller {

    Response doService(Request request) throws IOException;
}
