package nextstep.jwp.app.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.app.db.InMemoryUserRepository;
import nextstep.jwp.app.domain.User;
import nextstep.jwp.http.common.session.HttpCookie;
import nextstep.jwp.http.common.session.HttpSession;
import nextstep.jwp.http.common.session.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mvc.controller.AbstractController;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        Object user = request.getSession().getAttribute("user");
        if (Objects.nonNull(user)) {
            return response.sendRedirect(INDEX_HTML);
        }
        return response.forward(request.getPath() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        HttpResponse response = new HttpResponse();

        login(request, response, account, password);
        handleSession(request, response);

        return response;
    }

    private void login(HttpRequest request, HttpResponse response, String account, String password) {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

        if(foundUser.isPresent()) {
            User user = foundUser.get();
            if (user.checkPassword(password)) {
                final HttpSession session = request.getSession();
                session.setAttribute("user", foundUser);
                response.sendRedirect(INDEX_HTML);
                return;
            }
        }
        response.sendRedirect(ERROR_401_HTML);
    }

    private void handleSession(HttpRequest request, HttpResponse response) {
        String sessionId = request.getSession().getId();
        if (!HttpSessions.isValid(sessionId)) {
            HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", sessionId));
            cookie.addCookie(sessionId);
            response.setCookie(cookie);

            HttpSessions.putSession(request.getSession());
        }
    }
}
