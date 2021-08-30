package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.exception.HttpBodyException;

public class HttpBody {
    private final String body;
    private Map<String, String> bodyParamsMap = new HashMap<>();

    public HttpBody(String body) {
        this.body = body;
        extractBodyValue(body);
    }

    public void extractBodyValue(String body) {
        if (!body.isEmpty()) {
            String[] params = body.split("&");

            for (String keyValue : params) {
                String[] param = keyValue.split("=");
                bodyParamsMap.put(param[0], param[1]);
            }
        }
    }

    public String getBodyParams(String key) {
        if (bodyParamsMap.containsKey(key)) {
            return bodyParamsMap.get(key);
        }
        throw new HttpBodyException("body의 해당 키를 가지는 값이 존재하지 않습니다.");
    }
}
