package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.util.HttpRequestUtils;

import java.util.Map;

public class RequestBody {

    private final String message;

    public RequestBody(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getParameter(String param) {
        Map<String, String> params = HttpRequestUtils.parseQueryString(message);
        if (!params.containsKey(param)) {
            throw new IllegalArgumentException("해당 파라미터가 존재하지 않습니다. (입력 : " + param + ")");
        }
        return params.get(param);
    }
}
