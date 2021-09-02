package nextstep.jwp.handler.controller;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.handler.service.SessionHandler;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected ModelAndView doGet(HttpRequest request, HttpResponse response) {
        String sessionId = request.getSessionId();
        Object loginUser = SessionHandler.getSessionValueOrNull(sessionId, "user");
        if (Objects.isNull(loginUser)) {
            return ModelAndView.of("/login.html", HttpStatus.OK);
        }
        response.addHeader("Location", "index.html");
        return ModelAndView.of(HttpStatus.FOUND);
    }

    @Override
    protected ModelAndView doPost(HttpRequest request, HttpResponse response) {
        User loginUser = login(request.requestParam());
        if (Objects.isNull(loginUser)) {
            Model model = new Model();
            model.addAttribute("errorMessage", "유효하지 않은 사용자 정보입니다.");
            return ModelAndView.of(model, "/401.html", HttpStatus.UNAUTHORIZED);
        }

        HttpSession session = SessionHandler.getHttpSession(request, response);
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
