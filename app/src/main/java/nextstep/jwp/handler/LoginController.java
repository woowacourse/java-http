package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class LoginController implements Handler {

    private static final MappingPath PRINT_LOGIN_PAGE_PATH = MappingPath.of(HttpMethod.GET, SourcePath.of("/login"));
    private static final MappingPath LOGIN_POST_PATH = MappingPath.of(HttpMethod.POST, SourcePath.of("/login"));

    private final Map<MappingPath, BiFunction<HttpRequest, HttpResponse, ModelAndView>> handlers = new HashMap<>();
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
    public ModelAndView service(HttpRequest request, HttpResponse httpResponse) {
        return handlers.computeIfAbsent(MappingPath.of(request), key -> {
            throw new IncorrectHandlerException();
        }).apply(request, httpResponse);
    }

    private ModelAndView printLoginPage(HttpRequest request, HttpResponse httpResponse) {
        return ModelAndView.of("/login.html", HttpStatus.OK);
    }

    private ModelAndView login(HttpRequest request, HttpResponse httpResponse) {
        loginService.login(LoginRequest.fromHttpRequest(request));

        httpResponse.addHeader("Location", "index.html");
        return ModelAndView.of(Model.EMPTY, HttpStatus.FOUND);
    }
}
