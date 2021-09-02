package nextstep.jwp.web;

import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.exception.PageNotFoundError;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.CharlieHttpResponse;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.HttpStatusCode;

import java.util.Optional;

public class FrontController {
    private static final String INTERNAL_SERVER_ERROR_PAGE = "/500.html";
    private static final String NOT_FOUND_ERROR_PAGE = "/404.html";

    private final RequestMappingHandler requestMappingHandler;

    public FrontController(RequestMappingHandler requestMappingHandler) {
        this.requestMappingHandler = requestMappingHandler;
    }

    public HttpResponse getResponse(HttpRequest httpRequest) {
        try {
            Optional<ControllerMethod> optionalControllerMethod = requestMappingHandler.getControllerMethod(httpRequest);
            if (optionalControllerMethod.isPresent()) {
                ControllerMethod controllerMethod = optionalControllerMethod.orElseThrow(PageNotFoundError::new);
                String viewName = (String) controllerMethod.invoke(httpRequest);
                return CharlieHttpResponse.createResponse(viewName, HttpStatusCode.OK);
            }
            return CharlieHttpResponse.createResponse(httpRequest.getResourceName(), HttpStatusCode.OK);
        } catch (InternalServerError e) {
            return CharlieHttpResponse.createResponse(INTERNAL_SERVER_ERROR_PAGE, HttpStatusCode.INTERNAL_SERVER_ERROR);
        } catch (PageNotFoundError e) {
            return CharlieHttpResponse.createResponse(NOT_FOUND_ERROR_PAGE, HttpStatusCode.NOTFOUND);
        }
    }
}
