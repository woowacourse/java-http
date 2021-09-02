package nextstep.jwp.handler.controller;

import java.util.Objects;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.handler.service.SessionHandler;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class MainController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        String sessionId = request.getSessionId();
        Object loginUser = SessionHandler.getSessionValueOrNull(sessionId, "user");
        if (Objects.isNull(loginUser)) {
            return ModelAndView.of("/index.html", HttpStatus.OK);
        }

        Model model = new Model();
        model.addAttribute("userName", ((User) loginUser).getAccount());
        return ModelAndView.of(model, "/index.html", HttpStatus.OK);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        return ModelAndView.of(HttpStatus.NOT_FOUND);
    }
}
