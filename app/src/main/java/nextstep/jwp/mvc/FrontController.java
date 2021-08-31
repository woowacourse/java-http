package nextstep.jwp.mvc;

import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.exception.PageNotFoundError;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.CharlieHttpResponse;
import nextstep.jwp.response.HttpResponse;

import java.util.Optional;

public class FrontController {
    private static final String REDIRECT_FORM = "redirect: %s";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "/500.html";
    private static final String NOT_FOUND_ERROR_PAGE = "/404.html";

    public static final FrontController INSTANCE = new FrontController();

    private final RequestMappingHandler requestMappingHandler;

    public FrontController() {
        this.requestMappingHandler = RequestMappingHandler.getInstance();
    }

    public HttpResponse getResponse(HttpRequest httpRequest) {
        try {
            Optional<ControllerMethod> optionalControllerMethod = requestMappingHandler.getControllerMethod(httpRequest);
            if (optionalControllerMethod.isPresent()) {
                ControllerMethod controllerMethod = optionalControllerMethod.orElseThrow(PageNotFoundError::new);
                String viewName = (String) controllerMethod.invoke(httpRequest);
                return CharlieHttpResponse.createResponse(viewName);
            }
            return CharlieHttpResponse.okResponse(httpRequest.getResourceName());
        } catch (InternalServerError e) {
            return CharlieHttpResponse.redirectResponse(INTERNAL_SERVER_ERROR_PAGE);
        } catch (PageNotFoundError e) {
            return CharlieHttpResponse.redirectResponse(NOT_FOUND_ERROR_PAGE);
        }
    }

    public static FrontController getInstance() {
        return INSTANCE;
    }
}
