package nextstep.jwp.view;

public class ViewResolver {
    public View resolveViewName(String viewName) {
        return new View(viewName);
    }
}
