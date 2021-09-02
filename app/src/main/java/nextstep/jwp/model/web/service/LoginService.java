package nextstep.jwp.model.web.service;

import nextstep.jwp.model.User;
import nextstep.jwp.model.web.sessions.HttpSession;
import nextstep.jwp.model.web.sessions.HttpSessions;

import java.util.UUID;

import static nextstep.jwp.db.InMemoryUserRepository.existsAccount;
import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

public class LoginService {

    public boolean checkAccountExist(String account) {
        return existsAccount(account);
    }

    public String login(String account, String password) {
        User user = findByAccount(account);
        if (user.checkPassword(password)) {
            String sessionId = UUID.randomUUID().toString();
            HttpSession session = new HttpSession(sessionId);
            session.setAttribute(sessionId, user);
            HttpSessions.addSession("user", session);
            return sessionId;
        }
        throw new RuntimeException("invalid login");
    }
}
