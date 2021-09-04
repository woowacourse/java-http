package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

        throw new NoSuchElementException("[" + key + "]를 key로 가지는 HTTP Body에 value가 존재하지 않습니다.");
    }

    public String makeHttpMessage() {
        return body;
    }
}
