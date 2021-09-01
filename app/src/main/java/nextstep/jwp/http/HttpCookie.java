package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> value = new HashMap<>();

    public HttpCookie(String line) {
        String[] parameters = line.split("; ");
        for (String parameter : parameters) {
            String[] params = parameter.split("=");
            if (params.length == 2) {
                this.value.put(params[0], params[1]);
            }
        }
    }

    public String getAttribute(String key) {
        return this.value.get(key);
    }
}
