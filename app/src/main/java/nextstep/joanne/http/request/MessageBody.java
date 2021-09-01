package nextstep.joanne.http.request;

import java.util.Map;

public class MessageBody {
    private final Map<String, String> body;

    public MessageBody(Map<String, String> body) {
        this.body = body;
    }
}
