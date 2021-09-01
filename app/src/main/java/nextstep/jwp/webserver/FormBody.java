package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class FormBody {

    private Map<String, String> body;

    public FormBody() {
        this(new HashMap<>());
    }

    public FormBody(Map<String, String> body) {
        this.body = body;
    }

    public FormBody(String bodyString) {
        this(parseBody(bodyString));
    }

    private static Map<String, String> parseBody(String bodyString) {
        Map<String, String> body = new HashMap<>();
        for (String queryString : bodyString.split("&")) {
            int index = queryString.indexOf("=");
            String key = queryString.substring(0, index);
            String value = queryString.substring(index + 1).trim();
            body.put(key, value);
        }
        return body;
    }

    public String get(String name) {
        return body.get(name);
    }
}
