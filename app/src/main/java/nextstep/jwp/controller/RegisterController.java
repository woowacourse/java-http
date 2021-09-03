package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.CREATED;
import static nextstep.jwp.http.response.HttpStatus.OK;

import java.util.Optional;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.service.Service;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    private final Service service;

    public RegisterController(Service service) {
        this.service = service;
    }

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("Register - HTTP GET Request");

        HttpSession session = request.getSession();
        if (session == null) {
            return getRegisterPage(request, response);
        }
        Optional<User> findUser = session.getSession(request.getJSessionId());
        if (findUser.isEmpty()) {
            return getLoginPage(request, response);
        }
        return getHomePage(request, response, OK);
    }

    private View getRegisterPage(HttpRequest request, HttpResponse response) {
        response.forward(OK, request.getUri());
        return new View(request.getPath());
    }

    private View getLoginPage(HttpRequest request, HttpResponse response) {
        response.forward(OK, request.getUri());
        return new View(LOGIN_PATH);
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("Register - HTTP POST Request");

        service.doService(request, response);
        return getHomePage(request, response, CREATED);
    }

    private View getHomePage(HttpRequest request, HttpResponse response, HttpStatus httpStatus) {
        response.forward(httpStatus, request.getUri());
        return new View(HOME_PATH);
    }
}
