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
        // todo HTTP Message 객체 내부에서 발생하는 예외를 어떻게 다룰지 처리는 어디서, 어떻게 할 지 고민
        throw new HttpBodyException("body의 해당 키를 가지는 값이 존재하지 않습니다.");
    }
}
