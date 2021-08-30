package nextstep.jwp.httpmessage;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private final Map<String, String> parameters;

    public Parameters(Map<String, String> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
