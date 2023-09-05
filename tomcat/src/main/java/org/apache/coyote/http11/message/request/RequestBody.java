package org.apache.coyote.http11.message.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: 다른 점이 없으므로 RequestBody + ResponseBody 묶기
public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(List<String> lines) {
        Map<String, String> body = new HashMap<>();

        lines.stream()
                .map(each -> each.split("="))
                .forEach(each -> body.put(each[0], each[1]));

        return new RequestBody(body);
    }

    public static RequestBody ofEmpty() {
        return new RequestBody(Collections.emptyMap());
    }

    public String get(String key) {
        return body.get(key);
    }

}
