package nextstep.jwp.web;

import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.exception.PageNotFoundError;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.HttpStatusCode;

import java.util.Optional;

public class FrontController {
    private static final String INTERNAL_SERVER_ERROR_PAGE = "/500.html";
    private static final String NOT_FOUND_ERROR_PAGE = "/404.html";

    private final RequestMapping requestMapping;

    public FrontController(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Optional<ControllerMethod> optionalControllerMethod = requestMapping.getControllerMethod(httpRequest);
            if (optionalControllerMethod.isPresent()) {
                ControllerMethod controllerMethod = optionalControllerMethod.orElseThrow(PageNotFoundError::new);
                String viewName = (String) controllerMethod.invoke(httpRequest, httpResponse);
                httpResponse.setView(viewName, HttpStatusCode.OK);
                return;
            }
            httpResponse.setView(httpRequest.getResourceName(), HttpStatusCode.OK);
        } catch (InternalServerError e) {
            httpResponse.setView(INTERNAL_SERVER_ERROR_PAGE, HttpStatusCode.INTERNAL_SERVER_ERROR);
        } catch (PageNotFoundError e) {
            httpResponse.setView(NOT_FOUND_ERROR_PAGE, HttpStatusCode.NOTFOUND);
        }
    }
}
