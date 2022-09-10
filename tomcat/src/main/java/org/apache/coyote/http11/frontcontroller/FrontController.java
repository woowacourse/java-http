package org.apache.coyote.http11.frontcontroller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerAdvice;
import nextstep.jwp.exception.notfound.ControllerNotFoundException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.requestmapping.ApiHandlerMapper;
import org.apache.coyote.http11.requestmapping.FileHandlerMapper;
import org.apache.coyote.http11.requestmapping.RequestMapper;

public class FrontController {

    private static final List<RequestMapper> HANDLER_MAPPERS = List.of(new FileHandlerMapper(), new ApiHandlerMapper());
    private static final ControllerAdvice CONTROLLER_ADVICE = new ControllerAdvice();

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        try {
            Controller controller = getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception exception) {
            Exception targetException = getTargetException(exception);
            CONTROLLER_ADVICE.handleException(httpResponse, targetException);
        }
        httpResponse.write();
    }

    private Controller getController(HttpRequest httpRequest) {
        return HANDLER_MAPPERS.stream()
                .map(mapper -> mapper.mapController(httpRequest))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ControllerNotFoundException(httpRequest + "\n요청을 처리할 수 있는 controller가 없습니다."));
    }

    private Exception getTargetException(Exception exception) {
        if (exception instanceof InvocationTargetException) {
            InvocationTargetException invocationTargetException = (InvocationTargetException) exception;
            exception = (Exception) invocationTargetException.getTargetException();
        }
        return exception;
    }
}
