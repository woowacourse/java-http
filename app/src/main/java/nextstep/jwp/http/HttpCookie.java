package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final int KEY_INDEX = 0;
    private final int VALUE_INDEX = 1;
    private final int PARAM_LENGTH = 2;

    private final Map<String, String> value = new HashMap<>();

    public HttpCookie(String line) {
        if (line == null) {
            return;
        }
        String[] parameters = line.split("; ");
        for (String parameter : parameters) {
            String[] params = parameter.split("=");
            if (params.length == PARAM_LENGTH) {
                this.value.put(params[KEY_INDEX], params[VALUE_INDEX]);
            }
        }
    }

    public String getAttribute(String key) {
        return this.value.get(key);
    }
}
