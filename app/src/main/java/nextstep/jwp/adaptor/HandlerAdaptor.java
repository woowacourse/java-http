package nextstep.jwp.adaptor;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.ResponseEntity;
import nextstep.jwp.http.request.HttpRequest;

public interface HandlerAdaptor {
    ResponseEntity handle(Handler handler, HttpRequest httpRequest);
}
