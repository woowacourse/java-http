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
            String[] split = queryString.split("=");
            body.put(split[0], split[1]);
        }
        return body;
    }

    public static FormBody emptyBody() {
        return new FormBody();
    }

    public String get(String name) {
        return body.get(name);
    }
}
