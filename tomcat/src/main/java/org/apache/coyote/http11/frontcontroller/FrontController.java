package org.apache.coyote.http11.frontcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handlermapper.ApiHandlerMapper;
import org.apache.coyote.http11.handlermapper.FileHandlerMapper;
import org.apache.coyote.http11.handlermapper.HandlerMapper;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.view.ModelAndView;

public class FrontController {

    private static final List<HandlerMapper> HANDLER_MAPPERS = List.of(new FileHandlerMapper(), new ApiHandlerMapper());

    public HttpResponse doDispatch(HttpRequest httpRequest) throws IOException {
        Handler handler = getHandler(httpRequest);
        Object handlerResponse = handler.getResponse(httpRequest);
        return getHttp11Response(handlerResponse);
    }

    private Handler getHandler(HttpRequest httpRequest) {
        return HANDLER_MAPPERS.stream()
                .map(mapper -> mapper.mapHandler(httpRequest))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 요청이 아닙니다."));
    }

    private HttpResponse getHttp11Response(Object handlerResponse) throws IOException {
        ModelAndView modelAndView = ModelAndView.of(handlerResponse);
        return HttpResponse.of(modelAndView);
    }
}
