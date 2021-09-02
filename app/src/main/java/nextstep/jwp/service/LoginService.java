package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.model.domain.User;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.request.Session;

public class LoginService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public void loginByGet(Request request) {
        try {
            Map<String, String> queries = request.getQueries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            User user = getUser(account);
            checkPassword(user, password);
            Session session = request.getSession();
            session.setAttribute("user", user);
        } catch (IndexOutOfBoundsException | NullPointerException exception) {
            throw new LoginException();
        }
    }

    public void loginByPost(Request request) {
        try {
            Map<String, String> queries = request.getBody().queries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            User user = getUser(account);
            checkPassword(user, password);
            Session session = request.getSession();
            session.setAttribute("user", user);
        } catch (IndexOutOfBoundsException | NullPointerException exception) {
            throw new LoginException();
        }
    }

    private void checkPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new LoginException();
        }
    }

    private User getUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginException::new);
    }
}
