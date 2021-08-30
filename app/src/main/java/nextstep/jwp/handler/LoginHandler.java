package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.service.LoginService;

public class LoginHandler extends AbstractHandler {

    private final LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response getMessage(Request request) throws IOException {
        if (request.hasQueryString()) {
            loginService.loginByGet(request);
            return new Response(redirectMessage(PathType.INDEX.resource()));
        }
        final String responseBody = fileByPath(request.path() + FileType.HTML.extension());
        return new Response(staticFileMessage(FileType.HTML, responseBody));
    }

    @Override
    public Response postMessage(Request request) {
        loginService.loginByPost(request);
        return new Response(redirectMessage(PathType.INDEX.resource()));
    }
}
