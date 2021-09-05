package nextstep.jwp.handler.controller;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.model.User;

public class MainController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        if (session.contains("user")) {
            Model model = new Model();
            model.addAttribute("userName", ((User) session.getAttribute("user")).getAccount());
            return ModelAndView.of(model, "/index.html", HttpStatus.OK);
        }
        return ModelAndView.of("/index.html", HttpStatus.OK);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        return ModelAndView.of(HttpStatus.NOT_FOUND);
    }
}
