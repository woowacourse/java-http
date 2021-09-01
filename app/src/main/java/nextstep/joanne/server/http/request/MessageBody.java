package nextstep.joanne.server.http.request;

import java.util.Map;

public class MessageBody {
    private final Map<String, String> body;

    public MessageBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> body() {
        return body;
    }

    public String get(String key) {
        return body.getOrDefault(key, null);
    }
}
