package nextstep.jwp.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

import java.io.IOException;

public interface Controller {

    Response respond(Request request) throws IOException;
}
