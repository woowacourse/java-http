package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final Map<String, Object> model = new HashMap<>();

    private String viewName;
    private HttpStatus status;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getViewName() {
        return viewName;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
