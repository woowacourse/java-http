package nextstep.jwp.handler;

import nextstep.jwp.handler.modelandview.ModelAndView;

public class ControllerAdvice {

    public ModelAndView unauthorizedException() {
        return ModelAndView.unauthorized();
    }

    public ModelAndView userException() {
        return ModelAndView.badRequest();
    }

    public ModelAndView unhandledException() {
        return ModelAndView.error();
    }
}
