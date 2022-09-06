package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String REQUEST_BODY_VALUE_DELIMITER = "=";
    private static final String BLANK = "";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String value;
    private final Map<String, String> parseValue;
    
    public RequestBody(final String value) {
        this.value = value;
        this.parseValue = parseRequestBodyValue(value);
    }

    private Map<String, String> parseRequestBodyValue(final String value) {
        if (value.equals(BLANK)) {
            return new HashMap<>();
        }

        Map<String, String> bodyValues = new HashMap<>();
        String[] parseValues = value.split(REQUEST_BODY_DELIMITER);
        for (String parseValue : parseValues) {
            String[] values = parseValue.split(REQUEST_BODY_VALUE_DELIMITER);
            bodyValues.put(values[KEY_INDEX], values[VALUE_INDEX]);
        }

        return bodyValues;
    }

    public String getValue(String key) {
        return parseValue.get(key);
    }

    public String getValue() {
        return value;
    }
}
