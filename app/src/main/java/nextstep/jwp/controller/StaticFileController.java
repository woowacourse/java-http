package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class StaticFileController extends AbstractController {

    @Override
    public Response doGet(Request request) throws IOException {
        final String responseBody = fileByPath(request.path());
        return staticFileMessage(request, request.fileType(), responseBody);
    }
}
