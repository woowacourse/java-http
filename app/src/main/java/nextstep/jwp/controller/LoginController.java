package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.FOUND;
import static nextstep.jwp.http.response.HttpStatus.OK;
import static nextstep.jwp.http.response.HttpStatus.UNAUTHORIZED;

import java.util.Optional;
import nextstep.jwp.exception.controller.UnAuthorizationException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.service.Service;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final Service service;

    public LoginController(Service service) {
        this.service = service;
    }

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("Login - HTTP GET Request");

        HttpSession session = request.getSession();
        if (session == null) {
            return getLoginPage(request, response);
        }
        Optional<User> findUser = session.getSession(request.getJSessionId());
        if (findUser.isEmpty()) {
            getLoginPage(request, response);
        }
        return getHomePage(request, response, OK);
    }

    private View getLoginPage(HttpRequest request, HttpResponse response) {
        response.forward(OK, request.getUri());
        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("Login - HTTP POST Request");

        try {
            service.doService(request, response);
        } catch (UnAuthorizationException e) {
            return getUnauthorizedPage(response);
        }
        return getHomePage(request, response, FOUND);
    }

    private View getUnauthorizedPage(HttpResponse response) {
        response.forward(UNAUTHORIZED, UNAUTHORIZED_PATH);
        return new View(UNAUTHORIZED_PATH);
    }

    private View getHomePage(HttpRequest request, HttpResponse response, HttpStatus httpStatus) {
        if (FOUND.equals(httpStatus)) {
            response.redirect(FOUND, HOME_PATH);
            return new View(HOME_PATH);
        }
        response.forward(httpStatus, request.getUri());
        return new View(HOME_PATH);
    }
}
