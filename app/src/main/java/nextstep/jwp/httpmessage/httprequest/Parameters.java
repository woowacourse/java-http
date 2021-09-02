package nextstep.jwp.httpmessage.httprequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private final Map<String, String> values;

    public Parameters(Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(values.keySet());
    }

    public String getParameter(String name) {
        return values.get(name);
    }
}
