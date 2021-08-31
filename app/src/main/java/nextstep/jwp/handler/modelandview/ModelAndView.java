package nextstep.jwp.handler.modelandview;

import nextstep.jwp.http.response.HttpStatus;

public class ModelAndView {
    private final Model model;
    private final String viewName;
    private final HttpStatus httpStatus;

    public ModelAndView(Model model, String viewName, HttpStatus httpStatus) {
        this.model = model;
        this.viewName = viewName;
        this.httpStatus = httpStatus;
    }

    public static ModelAndView of(HttpStatus httpStatus) {
        return new ModelAndView(Model.EMPTY, "", httpStatus);
    }

    public static ModelAndView of(String viewName, HttpStatus httpStatus) {
        return new ModelAndView(Model.EMPTY, viewName, httpStatus);
    }

    public String getViewName() {
        return viewName;
    }

    public Model getModel() {
        return model;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
