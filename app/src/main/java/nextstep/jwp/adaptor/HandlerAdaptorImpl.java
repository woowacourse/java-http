package nextstep.jwp.adaptor;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.exception.UnauthorizedException;
import nextstep.jwp.handler.exception.UserException;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class HandlerAdaptorImpl implements HandlerAdaptor {
    public ModelAndView handle(Handler handler, HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            return handler.service(httpRequest, httpResponse);
        } catch (UnauthorizedException exception) {
            return ModelAndView.of("/401.html", HttpStatus.UNAUTHORIZED);
        } catch (UserException userException) {
            return ModelAndView.of("/404.html", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ModelAndView.of("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
