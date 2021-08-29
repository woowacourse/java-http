package nextstep.jwp.model.httpMessage;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, String> params = new HashMap<>();

    public RequestBody(Map<String, String> params) {
        this.params = params;
    }
}
