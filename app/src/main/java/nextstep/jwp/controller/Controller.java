package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.reponse.Response;
import nextstep.jwp.model.request.Request;

public interface Controller {

    Response doService(Request request) throws IOException;
}
