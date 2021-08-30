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
import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RegisterController implements Handler {

    public static final MappingPath PRINT_REGISTER_PAGE_PATH = MappingPath.of(HttpMethod.GET, SourcePath.of("/register"));
    public static final MappingPath REGISTER_POST_PATH = MappingPath.of(HttpMethod.POST, SourcePath.of("/register"));

    private final Map<MappingPath, BiFunction<HttpRequest, HttpResponse, ModelAndView>> handlers = new HashMap<>();

    public RegisterController() {
        handlers.put(PRINT_REGISTER_PAGE_PATH, this::printRegisterPage);
        handlers.put(REGISTER_POST_PATH, this::register);
    }

    @Override
    public boolean mapping(HttpRequest request) {
        return handlers.containsKey(MappingPath.of(request));
    }

    @Override
    public ModelAndView service(HttpRequest request, HttpResponse response) {
        return handlers.computeIfAbsent(MappingPath.of(request), key -> {
            throw new IncorrectHandlerException();
        }).apply(request, response);
    }

    private ModelAndView printRegisterPage(HttpRequest request, HttpResponse response) {
        return ModelAndView.of("/register.html", HttpStatus.OK);
    }

    private ModelAndView register(HttpRequest request, HttpResponse response) {
        QueryParams params = QueryParams.of(request.requestBody());
        User user = new User(params.get("account"), params.get("password"), params.get("email"));

        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            InMemoryUserRepository.save(user);
            response.addHeader("Location", "index.html");
            return ModelAndView.of(HttpStatus.FOUND);
        }
        return ModelAndView.of("/404.html", HttpStatus.NOT_FOUND);
    }
}
