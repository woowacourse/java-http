package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RegisterController implements Handler {

    public static final MappingPath PRINT_REGISTER_PAGE_PATH = MappingPath.of(HttpMethod.GET, SourcePath.of("/register"));
    public static final MappingPath REGISTER_POST_PATH = MappingPath.of(HttpMethod.POST, SourcePath.of("/register"));

    private final Map<MappingPath, Function<HttpRequest, ResponseEntity>> handlers = new HashMap<>();
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
        handlers.put(PRINT_REGISTER_PAGE_PATH, this::printRegisterPage);
        handlers.put(REGISTER_POST_PATH, this::register);
    }

    @Override
    public boolean mapping(HttpRequest httpRequest) {
        return handlers.containsKey(MappingPath.of(httpRequest));
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        return handlers.computeIfAbsent(MappingPath.of(httpRequest), key -> {
            throw new IncorrectHandlerException();
        }).apply(httpRequest);
    }

    private ResponseEntity printRegisterPage(HttpRequest httpRequest) {
        return ResponseEntity.ok("/register.html");
    }

    private ResponseEntity register(HttpRequest httpRequest) {
        registerService.register(RegisterRequest.fromHttpRequest(httpRequest));
        return ResponseEntity.redirect("index.html");
    }
}
