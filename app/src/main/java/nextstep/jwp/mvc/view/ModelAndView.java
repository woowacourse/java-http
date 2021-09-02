package nextstep.jwp.mvc.view;

public class ModelAndView {

    private String viewName;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }
}
