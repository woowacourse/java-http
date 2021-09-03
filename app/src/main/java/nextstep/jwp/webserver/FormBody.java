package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class FormBody {

    private final Map<String, String> body;

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
        for (String bodyEntity : bodyString.split("&")) {
            putBody(body, bodyEntity.trim());
        }
        return body;
    }

    private static void putBody(Map<String, String> body, String bodyString) {
        int index = bodyString.indexOf("=");
        String key = bodyString.substring(0, index);
        String value = bodyString.substring(index + 1).trim();
        body.put(key, value);
    }

    public String get(String name) {
        return body.get(name);
    }
}
