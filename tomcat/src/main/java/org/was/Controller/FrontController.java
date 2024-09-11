package org.was.Controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.was.Controller.exception.ControllerNotFoundException;
import org.was.Controller.exception.MethodNotAllowedException;
import org.was.converter.HttpResponseConverter;
import org.was.view.View;
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
            View view = viewResolver.getView(responseResult.getPath());
            responseConverter.convertResponse(response,
                    responseResult.getStatusCode(), responseResult.getHeaders(), view.getContent());

        } catch (ControllerNotFoundException e) {
            responseConverter.convertResponse(response, HttpStatusCode.NOT_FOUND, e.getMessage());

        } catch (MethodNotAllowedException e) {
            responseConverter.convertResponse(response, HttpStatusCode.METHOD_NOT_ALLOWED, e.getMessage());

        } catch (IOException e) {
            responseConverter.convertResponse(response, HttpStatusCode.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");
        }
    }
}
