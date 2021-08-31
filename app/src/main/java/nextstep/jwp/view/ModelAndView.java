package nextstep.jwp.view;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final Map<String, Object> model = new HashMap<>();

    private String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
