package nextstep.jwp.controller.modelview;

import java.util.HashMap;
import java.util.Map;

public class ModelView {

    private final String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
