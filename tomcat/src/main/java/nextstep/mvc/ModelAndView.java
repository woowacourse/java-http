package nextstep.mvc;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final String viewName;
    private final Map<String, String> model = new HashMap<>();

    public ModelAndView(final String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView() {
        this(null);
    }

    public void setAttribute(String key, String value) {
        model.put(key, value);
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, String> getModel() {
        return model;
    }
}
