package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RegisterController implements Handler {

    public static final MappingPath PRINT_REGISTER_PAGE_PATH = MappingPath.of(HttpMethod.GET, SourcePath.of("/register"));
    public static final MappingPath REGISTER_POST_PATH = MappingPath.of(HttpMethod.POST, SourcePath.of("/register"));

    private final Map<MappingPath, BiFunction<HttpRequest, HttpResponse, ModelAndView>> handlers = new HashMap<>();
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
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
        registerService.register(RegisterRequest.fromHttpRequest(request));

        response.addHeader("Location", "index.html");
        return ModelAndView.of(Model.EMPTY, HttpStatus.FOUND);
    }
}
