package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.modelandview.ModelAndView;
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
    public boolean mapping(RequestLine requestLine) {
        return requestLine.isFrom("get", "/register") || requestLine.isFrom("post", "/register");
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
        throw new IncorrectHandlerException();
    }

    private ModelAndView printRegisterPage() {
        return ModelAndView.ok("/register.html");
    }

    private ModelAndView register(String requestBody) {
        try{
            QueryParams queryParams = QueryParams.of(requestBody);
            registerService.register(RegisterRequest.fromQueryParams(queryParams));
            return ModelAndView.redirect("index.html");
        } catch (Exception e){
            e.printStackTrace();
            return ModelAndView.error();
        }
    }
}
