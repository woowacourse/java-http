package nextstep.jwp.presentation;

import nextstep.jwp.exception.BaseException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final RequestMapping requestMapping;
    private final ExceptionControllerAdvice exceptionControllerAdvice;

    public FrontController(RequestMapping requestMapping, ExceptionControllerAdvice exceptionControllerAdvice) {
        this.requestMapping = requestMapping;
        this.exceptionControllerAdvice = exceptionControllerAdvice;
    }

    public void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (BaseException e) {
            exceptionControllerAdvice.handleException(e, response);
        }
    }
}
