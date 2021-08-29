package nextstep.jwp.adaptor;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.ResponseEntity;
import nextstep.jwp.http.request.HttpRequest;

public class HandlerAdaptorImpl implements HandlerAdaptor {
    public ResponseEntity handle(Handler handler, HttpRequest httpRequest) {
        try {
            return handler.service(httpRequest);
        } catch (Exception e){
            return ResponseEntity.unhandledException();
        }
    }
}
