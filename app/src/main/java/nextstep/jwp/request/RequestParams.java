package nextstep.jwp.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private static final String VALUE_DELIMITER = ":";
    private static final String AND_DELIMITER = "&";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private String body;
    private Map<String, String> params;

    public RequestParams() {
        this.params = new HashMap<>();
    }

    public void addBody(String content) {
        this.body = content;
    }

    public void addParams(String key, String value) {
        this.params.put(key, value);
    }

    public void addParams(String data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        final String[] eachValues = data.split(AND_DELIMITER);
        addValues(eachValues);
    }

    private void addValues(String[] eachValues) {
        for (String eachValue : eachValues) {
            final String[] value = eachValue.split(VALUE_DELIMITER);
            addParams(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }
}
