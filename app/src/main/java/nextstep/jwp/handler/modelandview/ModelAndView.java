package nextstep.jwp.handler.modelandview;

public class ModelAndView {
    private Model model;
    private String viewName;

    public ModelAndView(Model model, String viewName) {
        this.model = model;
        this.viewName = viewName;
    }

    public ModelAndView(Model model) {
        this(model, "");
    }

    public String getViewName() {
        return viewName;
    }

    public Model getModel() {
        return model;
    }
}
