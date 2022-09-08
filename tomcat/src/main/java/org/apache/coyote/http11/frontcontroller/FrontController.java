package org.apache.coyote.http11.frontcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.requestmapping.ApiHandlerMapper;
import org.apache.coyote.http11.requestmapping.FileHandlerMapper;
import org.apache.coyote.http11.requestmapping.RequestMapper;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class FrontController {

    private static final List<RequestMapper> HANDLER_MAPPERS = List.of(new FileHandlerMapper(), new ApiHandlerMapper());

    public void doDispatch(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Controller controller = getController(httpRequest);
        controller.service(httpRequest, httpResponse);
        httpResponse.write();
    }

    private Controller getController(HttpRequest httpRequest) {
        return HANDLER_MAPPERS.stream()
                .map(mapper -> mapper.mapHandler(httpRequest))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 요청이 아닙니다."));
    }
}
