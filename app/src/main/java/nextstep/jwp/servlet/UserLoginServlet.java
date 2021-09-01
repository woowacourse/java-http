package nextstep.jwp.servlet;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.reponse.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.stateful.HttpSession;
import nextstep.jwp.http.stateful.HttpSessions;
import nextstep.jwp.model.User;
import nextstep.jwp.tomcat.Servlet;

public class UserLoginServlet extends Servlet {

    public UserLoginServlet() {
        super("/login");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String sessionId = httpRequest.getSessionId();
        httpResponse.setStatus(HttpStatus.OK);

        if (HttpSessions.existSession(sessionId)) {
            checkSessionUser(sessionId);
            httpResponse.addFile("/index.html");
            return;
        }
        httpResponse.addFile("/login.html");
    }

    private void checkSessionUser(String sessionId) {
        HttpSession session = HttpSessions.getSession(sessionId);
        User user = (User) session.getAttribute(User.class.getName());
        log.debug("세션으로 로그인 유지중! 유저 정보 : {}", user);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<User> possibleUser = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");

        if (isCorrectUser(httpRequest, possibleUser)) {
            log.debug("로그인 완료! 유저 정보 : {}", possibleUser);
            httpResponse.createSession(possibleUser.get());
            httpResponse.setHeader("Location", "index.html");
            return;
        }

        httpResponse.setHeader("Location", "401.html");
    }

    private boolean isCorrectUser(HttpRequest httpRequest, Optional<User> user) {
        return !user.isEmpty() && user.get().checkPassword(httpRequest.getParameter("password"));
    }

}