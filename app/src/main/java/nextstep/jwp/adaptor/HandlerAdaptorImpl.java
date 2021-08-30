package nextstep.jwp.adaptor;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.ResponseEntity;
import nextstep.jwp.handler.exception.UnauthorizedException;
import nextstep.jwp.handler.exception.UserException;
import nextstep.jwp.http.request.HttpRequest;

public class HandlerAdaptorImpl implements HandlerAdaptor {
    public ResponseEntity handle(Handler handler, HttpRequest httpRequest) {
        try {
            return handler.service(httpRequest);
        } catch (UnauthorizedException exception) {
            return ResponseEntity.unauthorized();
        } catch (UserException userException) {
            return ResponseEntity.notFoundException();
        } catch (Exception e) {
            return ResponseEntity.unhandledException();
        }
    }
}
