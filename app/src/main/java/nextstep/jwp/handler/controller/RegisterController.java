package nextstep.jwp.handler.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        return ModelAndView.of("/register.html", HttpStatus.OK);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        QueryParams params = request.requestParam();
        User user = new User(params.get("account"), params.get("password"), params.get("email"));

        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            InMemoryUserRepository.save(user);
            response.addHeader("Location", "index.html");
            return ModelAndView.of(HttpStatus.FOUND);
        }

        Model model = new Model();
        model.addAttribute("errorMessage", "가입 실패");
        return ModelAndView.of(model,"/400.html", HttpStatus.BAD_REQUEST);
    }
}
