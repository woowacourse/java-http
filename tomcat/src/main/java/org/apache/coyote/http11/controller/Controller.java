package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.Response;

import java.io.IOException;

public interface Controller {

    Response respond(Request request) throws IOException;
}
