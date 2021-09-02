package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response doGet(Request request) throws IOException {
        Session session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            return redirectMessage(request, PathType.INDEX.resource());
        }
        if (request.hasQueries()) {
            loginService.loginByGet(request);
            return redirectMessage(request, PathType.INDEX.resource());
        }
        final String responseBody = createResponseBody(request.getPath() + FileType.HTML.extension());
        return staticFileMessage(request, FileType.HTML, responseBody);
    }

    @Override
    public Response doPost(Request request) {
        loginService.loginByPost(request);
        return redirectMessage(request, PathType.INDEX.resource());
    }
}
