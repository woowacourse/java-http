package org.was.controller;

import static com.techcourse.controller.PagePath.INTERNAL_SERVER_ERROR_PAGE;
import static com.techcourse.controller.PagePath.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatusCode.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.was.controller.exception.ControllerNotFoundException;
import org.was.controller.exception.MethodNotAllowedException;
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

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Controller controller = requestMapping.getController(request);
            ResponseResult responseResult = controller.service(request);
            responseConverter.convert(response, viewResolver, responseResult);

        } catch (ControllerNotFoundException e) {
            responseNotFound(response);

        } catch (MethodNotAllowedException e) {
            responseMethodNotAllowed(response, e);

        } catch (IOException e) {
            responseInternalServerError(response);
        }
    }

    private void responseNotFound(HttpResponse response) throws IOException {
        ResponseResult responseResult = ResponseResult
                .status(NOT_FOUND)
                .path(NOT_FOUND_PAGE.getPath());
        responseConverter.convert(response, viewResolver, responseResult);
    }

    private void responseMethodNotAllowed(HttpResponse response, MethodNotAllowedException e) {
        responseConverter.convert(response, METHOD_NOT_ALLOWED, e.getMessage());
    }

    private void responseInternalServerError(HttpResponse response) throws IOException {
        ResponseResult responseResult = ResponseResult
                .status(INTERNAL_SERVER_ERROR)
                .path(INTERNAL_SERVER_ERROR_PAGE.getPath());
        responseConverter.convert(response, viewResolver, responseResult);
    }
}
