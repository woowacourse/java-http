package nextstep.jwp.handler;

import nextstep.jwp.http.response.HttpStatus;

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

    public static ModelAndView ok(String viewName) {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.OK);
        return new ModelAndView(model, viewName);
    }

    public static ModelAndView redirect(String location) {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.FOUND);
        model.addAttribute("Location", location);
        return new ModelAndView(model);
    }

    public static ModelAndView unauthorized() {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.UNAUTHORIZED);
        return new ModelAndView(model, "/401.html");
    }

    public static ModelAndView error() {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.ERROR);
        return new ModelAndView(model, "/500.html");
    }

    public String getViewName() {
        return viewName;
    }

    public Model getModel() {
        return model;
    }
}
