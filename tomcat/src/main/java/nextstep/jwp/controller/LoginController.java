package nextstep.jwp.controller;

import common.http.AbstractController;
import common.http.Cookies;
import common.http.HttpStatus;
import common.http.Request;
import common.http.Response;
import common.http.Session;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static common.http.HttpStatus.FOUND;
import static common.http.HttpStatus.OK;
import static common.http.HttpStatus.UNAUTHORIZED;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(Request request, Response response) {
        if (!request.hasValidSession()) {
            buildStaticResourceResponse(request, response, OK, "/login.html");
            return;
        }

        Session session = request.getSession();
        buildRedirectResponse(request, response, session);
    }

    @Override
    protected void doPost(Request request, Response response) {
        User user = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        if (!user.checkPassword(request.getPassword())) {
            buildStaticResourceResponse(request, response, UNAUTHORIZED, "/401.html");
            return;
        }

        log.info("user: {}", user);
        Session session = request.getSession(true);
        session.setAttribute("user", user);
        buildRedirectResponse(request, response, session);
    }

    private void buildStaticResourceResponse(Request request, Response response, HttpStatus httpStatus, String path) {
        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(httpStatus);
        response.addStaticResourcePath(path);
    }

    private void buildRedirectResponse(Request request, Response response, Session session) {
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(FOUND);
        response.sendRedirect("/index.html");
    }
}
