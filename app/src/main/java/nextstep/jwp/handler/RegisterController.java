package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.exception.UserException;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;

public class RegisterController implements Handler {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public boolean mapping(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        return requestLine.isPath("/register");
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return printRegisterPage();
        }

        if (httpRequest.isPost()) {
            String requestBody = httpRequest.requestBody();
            return register(QueryParams.of(requestBody));
        }

        throw new IncorrectHandlerException();
    }

    private ResponseEntity printRegisterPage() {
        return ResponseEntity.ok("/register.html");
    }

    private ResponseEntity register(QueryParams queryParams) {
        try {
            registerService.register(RegisterRequest.fromQueryParams(queryParams));
            return ResponseEntity.redirect("index.html");
        } catch (UserException userException) {
            return ResponseEntity.userException();
        }
    }
}
