package nextstep.jwp.service;

import java.util.UUID;
import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.controller.response.LoginResponse;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.model.User;
import nextstep.jwp.server.HttpSession;
import nextstep.jwp.server.HttpSessions;

public class LoginService {

    private static final String SESSION_PARAMETER = "JSESSIONID";

    private final InMemoryUserRepository userRepository;
    private final HttpSessions httpSessions;

    public LoginService(InMemoryUserRepository userRepository, HttpSessions httpSessions) {
        this.userRepository = userRepository;
        this.httpSessions = httpSessions;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = findUserByAccount(loginRequest.getAccount());
        user.checkPassword(loginRequest.getPassword());

        UUID uuid = UUID.randomUUID();
        HttpSession httpSession = new HttpSession(uuid.toString());
        httpSession.setAttribute("user", user);
        httpSessions.addSession(httpSession);

        return new LoginResponse(SESSION_PARAMETER, httpSession.getId());
    }

    private User findUserByAccount(String account) {
        return userRepository.findByAccount(account)
            .orElseThrow(UnauthorizedException::new);
    }

    public boolean isAlreadyLogin(HttpCookie httpCookie) {
        String sessionId = httpCookie.getParameter(SESSION_PARAMETER);

        return httpSessions.hasObject(sessionId, "user");
    }
}
