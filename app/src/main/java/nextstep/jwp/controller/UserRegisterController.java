package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserRequest;
import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;
import nextstep.jwp.service.UserService;

public class UserRegisterController extends AbstractController {

    protected UserRegisterController() {
    }

    @Override
    protected ModelView doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        return new ModelView("/register.html");
    }

    @Override
    protected ModelView doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        UserService.register(new UserRequest(httpRequest.getParameter("account"),
                httpRequest.getParameter("password"),
                httpRequest.getParameter("email")));
        httpResponse.setHttpStatusCode(HttpStatusCode.FOUND);
        return new ModelView("/index.html");
    }
}
