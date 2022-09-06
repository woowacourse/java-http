package nextstep.jwp.service;

import static org.apache.coyote.Constants.CRLF;

import nextstep.jwp.db.Cookies;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.mapping.ResponseEntity;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public ResponseEntity login(HttpRequest request) {
        try {
            Query query = Query.ofQuery(request.getBody());

            User user = InMemoryUserRepository.findByAccount(query.find("account"))
                    .orElseThrow(NoUserException::new);
            validatePassword(query, user);

            final var session = request.getSession();
            addSession(user, session);
            LOG.info("SessionCount: " + SessionManager.get().size());

            return ResponseEntity.found("/index.html")
                    .addCookie(Cookies.ofJSessionId(session.getId()));
        } catch (NoUserException | InvalidPasswordException e) {
            return ResponseEntity.found("/401.html");
        }
    }

    private void addSession(User user, Session session) {
        session.setAttribute("user", user);
        SessionManager sessionManager = SessionManager.get();
        removeExistSession(session, sessionManager);
        sessionManager.add(session);
        LOG.info(CRLF + "로그인 성공! id : " + user.getAccount() + CRLF);
    }

    private void removeExistSession(Session session, SessionManager sessionManager) {
        Session existSession = sessionManager.searchByUser((User) session.getAttribute("user"));
        if (existSession != null) {
            sessionManager.remove(existSession);
        }
    }

    private void validatePassword(Query query, User user) {
        if (!user.checkPassword(query.find("password"))) {
            throw new InvalidPasswordException();
        }
    }

    public ResponseEntity register(HttpRequest request) {
        Query query = Query.ofQuery(request.getBody());

        String account = query.find("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return ResponseEntity.found("/register.html");
        }
        User user = new User(account, query.find("password"), query.find("email"));
        InMemoryUserRepository.save(user);
        LOG.info(CRLF + "회원가입 성공! id : " + user.getAccount() + CRLF);

        return ResponseEntity.found("/index.html");
    }
}
