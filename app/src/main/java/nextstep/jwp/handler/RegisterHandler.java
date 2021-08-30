package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.service.RegisterService;

public class RegisterHandler extends AbstractHandler {

    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Response getMessage(Request request) throws IOException {
        final String responseBody = fileByPath(request.path() + FileType.HTML.extension());
        return staticFileMessage(FileType.HTML, responseBody);
    }

    @Override
    public Response postMessage(Request request) {
        registerService.register(request);
        return redirectMessage(PathType.INDEX.resource());
    }
}
