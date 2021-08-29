package nextstep.jwp.handler;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.response.HttpStatus;

public class ResponseEntity {
    private final ModelAndView modelAndView;

    public ResponseEntity(ModelAndView modelAndView) {
        this.modelAndView = modelAndView;
    }

    public static ResponseEntity ok(String viewPath) {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.OK);
        ModelAndView modelAndView = new ModelAndView(model, viewPath);
        return new ResponseEntity(modelAndView);
    }

    public static ResponseEntity redirect(String location) {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.FOUND);
        model.addAttribute("Location", location);
        ModelAndView modelAndView = new ModelAndView(model);
        return new ResponseEntity(modelAndView);
    }

    public static ResponseEntity unauthorized() {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.UNAUTHORIZED);
        ModelAndView modelAndView = new ModelAndView(model, "/401.html");
        return new ResponseEntity(modelAndView);
    }

    public static ResponseEntity unhandledException() {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.ERROR);
        ModelAndView modelAndView = new ModelAndView(model, "/500.html");
        return new ResponseEntity(modelAndView);
    }

    public static ResponseEntity badRequest() {
        Model model = new Model();
        model.addAttribute("HttpStatus", HttpStatus.BAD_REQUEST);
        ModelAndView modelAndView = new ModelAndView(model, "/404.html");
        return new ResponseEntity(modelAndView);
    }

    public ModelAndView modelAndView() {
        return modelAndView;
    }
}
