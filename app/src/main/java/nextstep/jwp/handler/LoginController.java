package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LoginController implements Handler {

    private static final MappingPath PRINT_LOGIN_PAGE_PATH = MappingPath.of(HttpMethod.GET, SourcePath.of("/login"));
    private static final MappingPath LOGIN_POST_PATH = MappingPath.of(HttpMethod.POST, SourcePath.of("/login"));

    private final Map<MappingPath, Function<HttpRequest, ResponseEntity>> handlers = new HashMap<>();
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
        handlers.put(PRINT_LOGIN_PAGE_PATH, this::printLoginPage);
        handlers.put(LOGIN_POST_PATH, this::login);
    }

    @Override
    public boolean mapping(HttpRequest request) {
        return handlers.containsKey(MappingPath.of(request));
    }

    @Override
    public ResponseEntity service(HttpRequest request) {
        return handlers.computeIfAbsent(MappingPath.of(request), key -> {
            throw new IncorrectHandlerException();
        }).apply(request);
    }

    private ResponseEntity printLoginPage(HttpRequest request) {
        return ResponseEntity.ok("/login.html");
    }

    private ResponseEntity login(HttpRequest request) {
        loginService.login(LoginRequest.fromHttpRequest(request));
        return ResponseEntity.redirect("index.html");
    }
}
