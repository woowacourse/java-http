package nextstep.jwp.handler;

import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;

public class RegisterController implements Controller {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public boolean mapping(RequestLine requestLine) {
        return requestLine.isFrom("get", "/register")
                || requestLine.isFrom("post", "/register");
    }

    @Override
    public ModelAndView service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        if (requestLine.isFrom("get", "/register")) {
            return printRegisterPage();
        }
        if (requestLine.isFrom("post", "/register")) {
            return register(httpRequest.requestBody());
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }

    private ModelAndView printRegisterPage() {
        return ModelAndView.ok("/register.html");
    }

    private ModelAndView register(String requestBody) {
        try{
            QueryParams queryParams = QueryParams.of(requestBody);
            RegisterRequest registerRequest = RegisterRequest.fromQueryParams(queryParams);

            registerService.register(registerRequest);
            return ModelAndView.redirect("index.html");
        } catch (Exception e){
            return ModelAndView.error();
        }
    }
}
