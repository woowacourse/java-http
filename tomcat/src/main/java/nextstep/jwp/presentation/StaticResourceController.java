package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class StaticResourceController extends AbstractController {

    @Override
    protected ResponseEntity doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/404.html");
    }

    @Override
    protected ResponseEntity doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return new ResponseEntity(StatusCode.OK, httpRequest.getUrl());
    }
}
