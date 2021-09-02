package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.response.Response;
import nextstep.jwp.service.RegisterService;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Response doGet(Request request) throws IOException {
        final String responseBody = createResponseBody(request.getPath() + FileType.HTML.extension());
        return staticFileMessage(request, FileType.HTML, responseBody);
    }

    @Override
    public Response doPost(Request request) {
        registerService.register(request);
        return redirectMessage(request, PathType.INDEX.resource());
    }
}
