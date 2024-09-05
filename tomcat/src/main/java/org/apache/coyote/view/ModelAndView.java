package org.apache.coyote.view;

import java.util.Map;

public class ModelAndView {

    private final String view;
    private final Map<String, Object> model;

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getAttributes() {
        return this.model;
    }
}
