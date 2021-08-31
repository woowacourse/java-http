package nextstep.jwp.handler.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class LoginController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        return ModelAndView.of("/login.html", HttpStatus.OK);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        QueryParams params = QueryParams.of(request.requestBody());

        if (checkValidUser(params.get("account"), params.get("password"))) {
            response.addHeader("Location", "index.html");
            return ModelAndView.of(HttpStatus.FOUND);
        }
        return ModelAndView.of("/401.html", HttpStatus.UNAUTHORIZED);
    }

    private boolean checkValidUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
