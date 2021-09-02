package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.domain.User;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.request.Session;
import nextstep.jwp.model.response.Response;
import nextstep.jwp.model.response.StatusType;
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
            loginService.login(request.getQueries(), request.getSession());
            return redirectMessage(request, PathType.INDEX.resource());
        }
        final String responseBody = createResponseBody(request.getPath() + FileType.HTML.extension());
        return staticFileMessage(request, StatusType.OK, FileType.HTML, responseBody);
    }

    @Override
    public Response doPost(Request request) {
        loginService.login(request.getBodyQueries(), request.getSession());
        return redirectMessage(request, PathType.INDEX.resource());
    }
}
