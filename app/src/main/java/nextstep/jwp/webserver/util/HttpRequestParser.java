package nextstep.jwp.webserver.util;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static Map<String, String> parseValues(String valueLine, String regex) {
        final String[] values = valueLine.split(regex);
        return getKeyAndValue(values);
    }

    private static Map<String, String> getKeyAndValue(String[] eachValues) {
        Map<String, String> keyAndValue = new HashMap<>();
        for (String eachValue : eachValues) {
            final String[] value = eachValue.split(VALUE_DELIMITER);
            String key = value[KEY_INDEX].trim();
            String val = parseValue(value);
            keyAndValue.put(key, val);
        }
        return keyAndValue;
    }

    private static String parseValue(String[] value) {
        if(value.length < 2) {
            return "";
        }
        return value[VALUE_INDEX];
    }
}
