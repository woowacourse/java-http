package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
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

    public LoginController() {
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
        QueryParams params = QueryParams.of(request.requestBody());

        if (checkValidUser(params.get("account"), params.get("password"))) {
            httpResponse.addHeader("Location", "index.html");
            return ModelAndView.of(HttpStatus.FOUND);
        }
        return ModelAndView.of("/401.html", HttpStatus.UNAUTHORIZED);
    }

    private boolean checkValidUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
