package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserRequest;
import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.HttpSession;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.util.Objects;

public class UserLoginController extends AbstractController {

    protected UserLoginController() {
    }

    @Override
    protected ModelView doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.hasSession()) {
            final User user = getUser(httpRequest.getHttpSession());
            if (Objects.nonNull(user)) {
                httpResponse.setHttpStatusCode(HttpStatusCode.FOUND);
                return new ModelView("/index.html");
            }
        }
        return new ModelView("/login.html");
    }

    @Override
    protected ModelView doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        final User user = UserService.login(new UserRequest(httpRequest.getParameter("account"),
                httpRequest.getParameter("password")));
        final HttpSession httpSession = httpRequest.getHttpSession();
        httpSession.setAttribute("user", user);
        httpResponse.setHttpStatusCode(HttpStatusCode.FOUND);
        return new ModelView("/index.html");
    }

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
