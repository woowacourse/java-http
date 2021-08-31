package nextstep.jwp.controller;

import nextstep.jwp.model.FileType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class BaseController extends AbstractController {

    public static final String BASE_RESPONSE_BODY = "Hello world!";

    @Override
    public Response doGet(Request request) {
        return staticFileMessage(request, FileType.HTML, BASE_RESPONSE_BODY);
    }
}
