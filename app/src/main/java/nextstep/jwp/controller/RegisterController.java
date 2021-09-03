package nextstep.jwp.controller;

import nextstep.jwp.exception.AlreadyExistUserException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RegisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
    private static RegisterController registerController = null;

    private final UserService userService;

    private RegisterController(UserService userService) {
        this.userService = userService;
    }

    public static void createInstance(UserService userService) {
        if (Objects.isNull(registerController)) {
            registerController = new RegisterController(userService);
        }
    }

    public static RegisterController getInstance() {
        return Objects.requireNonNull(registerController);
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("HTTP GET Register Request: {}", request.getPath());
        response.responseOk("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("HTTP POST Register Request: {}", request.getPath());
        try {
            userService.signUp(request);
            response.responseRedirect("http://" + request.getHeader("Host") + "/index.html");
        } catch (AlreadyExistUserException exception) {
            response.responseRedirect("http://" + request.getHeader("Host") + "/register.html");
        }
    }
}
