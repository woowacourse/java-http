package org.apache.coyote.http11.web;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.ControllerMapper;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;

public class RequestHandler {

    public HttpResponse handle(final HttpRequest httpRequest) throws NotFoundException, IOException {
        final String uri = httpRequest.getUri();
        final Controller controller = ControllerMapper.findController(uri)
                .orElseThrow(() -> new NotFoundException("요청에 맞는 controller를 찾을 수 없습니다."));

        return controller.service(httpRequest);
    }
}
