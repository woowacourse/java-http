package nextstep.jwp.adaptor;

import nextstep.jwp.handler.ControllerAdvice;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.exception.UnauthorizedException;
import nextstep.jwp.handler.exception.UserException;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;

public class HandlerAdaptorImpl implements HandlerAdaptor {
    private final ControllerAdvice controllerAdvice = new ControllerAdvice();

    public ModelAndView handle(Handler handler, HttpRequest httpRequest) {
        try {
            return handler.service(httpRequest);
        } catch (UnauthorizedException exception) {
            return controllerAdvice.unauthorizedException();
        } catch (UserException userException) {
            return controllerAdvice.userException();
        } catch (Exception e) {
            e.printStackTrace();
            return controllerAdvice.unhandledException();
        }
    }
}
