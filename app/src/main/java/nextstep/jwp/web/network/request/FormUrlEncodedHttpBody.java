package nextstep.jwp.web.network.request;

import java.util.HashMap;
import java.util.Map;

public class FormUrlEncodedHttpBody implements HttpBody {

    private static final String DEFAULT_ATTRIBUTE_VALUE = null;
    private static final int SPLIT_INTO_TWO = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public FormUrlEncodedHttpBody(Map<String, String> body) {
        this.body = body;
    }

    public static FormUrlEncodedHttpBody of(String rawBody) {
        return new FormUrlEncodedHttpBody(parse(rawBody));
    }

    private static Map<String, String> parse(String rawBody) {
        final Map<String, String> bodyAsMap = new HashMap<>();
        final String[] params = rawBody.split("&");
        for (String param : params) {
            final String[] keyAndValue = param.split("=", SPLIT_INTO_TWO);
            bodyAsMap.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return bodyAsMap;
    }

    @Override
    public String getAttribute(String key) {
        return body.getOrDefault(key, DEFAULT_ATTRIBUTE_VALUE);
    }
}
