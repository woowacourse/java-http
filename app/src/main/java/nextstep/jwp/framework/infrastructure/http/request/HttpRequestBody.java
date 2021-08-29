package nextstep.jwp.framework.infrastructure.http.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestBody {

    private final String content;

    public HttpRequestBody(String content) {
        this.content = content;
    }

    public Map<String, String> getContentAsAttributes() {
        Map<String, String> attributes = new LinkedHashMap<>();
        String[] splits = content.split("&");
        for (String split : splits) {
            String[] st = split.split("=");
            attributes.put(st[0], st[1]);
        }
        return attributes;
    }
}
