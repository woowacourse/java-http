package nextstep.jwp.service;

import static nextstep.jwp.model.UserInfo.ACCOUNT;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.controller.UnAuthorizationException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class LoginService implements Service {

    @Override
    public void doService(HttpRequest request, HttpResponse response) {
        Map<String, String> query = request.getQuery();
        Optional<User> findUser = InMemoryUserRepository
            .findByAccount(query.get(ACCOUNT.getInfo()));

        if (findUser.isPresent()) {
            User user = findUser.get();
            HttpSession session = request.getSession();

            if (session == null) {
                String jSessionId = getUUID().toString();
                session = new HttpSession(jSessionId);

                request.saveSession(session);
                setCookie(request, response, jSessionId);

                saveUser(session, jSessionId, user);
                return;
            }
            saveUser(session, request.getJSessionId(), user);
        }
        throw new UnAuthorizationException();
    }

    private UUID getUUID() {
        return UUID.randomUUID();
    }

    private void setCookie(HttpRequest request, HttpResponse response, String jSessionId) {
        if (!request.isJSessionId()) {
            response.setCookie(jSessionId);
        }
    }

    private void saveUser(HttpSession session, String id, User user) {
        session.saveUser(id, user);
    }
}
