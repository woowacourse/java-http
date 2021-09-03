package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.exception.MethodArgumentNotValidException;
import nextstep.joanne.dashboard.service.RegisterService;
import nextstep.joanne.server.handler.controller.AbstractController;
import nextstep.joanne.server.http.HttpSession;
import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        log.debug("HTTP POST Register Request from {}", request.uri());

        final String account = request.bodyOf("account");
        final String email = request.bodyOf("email");
        final String password = request.bodyOf("password");

        if (Objects.isNull(account) || Objects.isNull(email) || Objects.isNull(password)) {
            throw new MethodArgumentNotValidException();
        }

        registerService.join(account, email, password);

        response.addStatus(HttpStatus.FOUND);
        response.addHeaders("Location", "/index.html");
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Register Request from {}", request.uri());

        HttpSession session = request.getSession();
        session.removeAttribute("user");

        response.addStatus(HttpStatus.OK);
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
        response.addBody(request.uri());
    }
}
