package nextstep.jwp.webserver.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private static final String VALUE_DELIMITER = "=";
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
            String key = value[KEY_INDEX];
            String val = parseValue(value);
            addParams(key, val);
        }
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public Map<String, String> getParams() {
        return params;
    }

    private String parseValue(String[] value) {
        if(value.length < 2) {
            return "";
        }
        return value[VALUE_INDEX];
    }
}
