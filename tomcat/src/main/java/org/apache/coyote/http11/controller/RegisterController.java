package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.Http11Processor.SESSION_MANAGER;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Session;

public class RegisterController extends AbstractController {

    private static final String MAIN_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> userInfos = request.getBody().parseUserInfos();
        if (InMemoryUserRepository.isNonExistentByAccount(userInfos.get("account"))) {
            final User user = new User(userInfos.get("account"), userInfos.get("password"), userInfos.get("email"));
            InMemoryUserRepository.save(user);
            response.setHttpStatus(HttpStatus.FOUND);
            response.sendRedirect(MAIN_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.OK);
        response.setPath(request.getUri().getPath());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.hasJSessionId()) {
            final Session session = SESSION_MANAGER.findSession(request.getJSessionId());
            validateExistentSession(session);
            response.setHttpStatus(HttpStatus.FOUND);
            response.sendRedirect(MAIN_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.OK);
        response.setPath(REGISTER_PAGE);
    }

    private void validateExistentSession(final Session session) {
        if (session == null) {
            throw new IllegalArgumentException("존재하지 않는 세션입니다.");
        }
    }
}
