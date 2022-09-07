package org.apache.coyote.http11.frontcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handlermapper.ApiHandlerMapper;
import org.apache.coyote.http11.handlermapper.FileHandlerMapper;
import org.apache.coyote.http11.handlermapper.HandlerMapper;
import org.apache.coyote.http11.httpmessage.request.Http11Version;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusLine;
import org.apache.coyote.http11.view.ModelAndView;

public class FrontController {

    private static final List<HandlerMapper> HANDLER_MAPPERS = List.of(new FileHandlerMapper(), new ApiHandlerMapper());

    public void doDispatch(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Handler handler = getHandler(httpRequest);
        Object handlerResponse = handler.handle(httpRequest);
        ModelAndView modelAndView = ModelAndView.of(handlerResponse);

        setHttpResponse(httpResponse, modelAndView);

        httpResponse.write();
    }

    private Handler getHandler(HttpRequest httpRequest) {
        return HANDLER_MAPPERS.stream()
                .map(mapper -> mapper.mapHandler(httpRequest))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 요청이 아닙니다."));
    }

    private void setHttpResponse(HttpResponse httpResponse, ModelAndView modelAndView) {
        httpResponse
                .setStatusLine(new StatusLine(Http11Version.HTTP_11_VERSION, modelAndView.getHttpStatus()))
                .setHeaders(modelAndView.getHeaders())
                .setResponseBody(modelAndView.getView());
    }
}
