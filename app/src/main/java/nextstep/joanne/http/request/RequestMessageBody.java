package nextstep.joanne.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestMessageBody {
    private final HashMap<String, String> messageBody;

    public RequestMessageBody(Map<String, String> messageBody) {
        this.messageBody = (HashMap<String, String>) messageBody;
    }

    public String valueFromKey(String key) {
        return messageBody.get(key);
    }
}
