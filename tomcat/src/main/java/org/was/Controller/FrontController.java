package org.was.Controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.was.Controller.exception.ControllerNotFoundException;
import org.was.Controller.exception.MethodNotAllowedException;
import org.was.converter.HttpResponseConverter;
import org.was.view.ViewResolver;

public class FrontController {

    private static final FrontController INSTANCE = new FrontController();

    private final RequestMapping requestMapping = RequestMapping.getInstance();
    private final ViewResolver viewResolver = ViewResolver.getInstance();
    private final HttpResponseConverter responseConverter = HttpResponseConverter.getInstance();

    private FrontController() {
    }

    public static FrontController getInstance() {
        return INSTANCE;
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = requestMapping.getController(request);
            ResponseResult responseResult = controller.service(request);
            responseConverter.convert(response, viewResolver, responseResult);

        } catch (ControllerNotFoundException e) {
            responseConverter.convert(response, HttpStatusCode.NOT_FOUND, "/404.html");

        } catch (MethodNotAllowedException e) {
            responseConverter.convert(response, HttpStatusCode.METHOD_NOT_ALLOWED, e.getMessage());

        } catch (IOException e) {
            responseConverter.convert(response, HttpStatusCode.INTERNAL_SERVER_ERROR, "/500.html");
        }
    }
}
