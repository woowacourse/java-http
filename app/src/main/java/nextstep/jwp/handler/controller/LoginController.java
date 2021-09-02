package nextstep.jwp.handler.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.jwp.model.User;

import java.util.Objects;

public class LoginController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        if(Objects.isNull(session.getAttribute("user"))){
            return ModelAndView.of("/login.html", HttpStatus.OK);
        }
        response.addHeader("Location", "index.html");
        return ModelAndView.of(HttpStatus.FOUND);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        User loginUser = login(request.requestParam());
        if (Objects.isNull(loginUser)) {
            return ModelAndView.of("/401.html", HttpStatus.UNAUTHORIZED);
        }

        HttpSession session = request.getSession();
        if (!HttpSessions.contains(session)) {
            HttpSessions.register(session);
            response.setCookie(HttpSession.SESSION_TYPE, session.getId());
        }
        session.setAttribute("user", loginUser);
        response.addHeader("Location", "index.html");
        return ModelAndView.of(HttpStatus.FOUND);
    }

    private User login(QueryParams params) {
        String account = params.get("account");
        String password = params.get("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElse(null);
    }
}
