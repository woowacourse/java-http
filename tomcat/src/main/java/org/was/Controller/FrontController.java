package org.was.Controller;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.was.Controller.exception.ControllerNotFoundException;
import org.was.Controller.exception.MethodNotAllowedException;

public class FrontController {

    private static final FrontController INSTANCE = new FrontController();

    private final RequestMapping requestMapping = RequestMapping.getInstance();

    private FrontController() {
    }

    public static FrontController getInstance() {
        return INSTANCE;
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = requestMapping.getController(request);
            ResponseResult responseResult = controller.service(request);
            response.setStatusCode(responseResult.getStatusCode());
            response.setHeader(responseResult.getHeader());
            response.setBody(responseResult.getBody());

        } catch (ControllerNotFoundException e) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
            response.setBody(new ResponseBody(e.getMessage()));

        } catch (MethodNotAllowedException e) {
            response.setStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED);
            response.setBody(new ResponseBody(e.getMessage()));
        }
    }
}
