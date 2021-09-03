package nextstep.jwp.httpserver.domain.view;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String viewName;
    private Map<String, Object> model;

    public ModelAndView(String viewName) {
        this(viewName, new HashMap<>());
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public boolean isResourceFile() {
        return viewName.endsWith(".html") || viewName.endsWith(".css")
                || viewName.endsWith(".js") || viewName.endsWith(".svg");
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
