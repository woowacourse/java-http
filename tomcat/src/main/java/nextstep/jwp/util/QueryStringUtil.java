package nextstep.jwp.util;

import java.util.HashMap;
import java.util.Map;

public class QueryStringUtil {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    public static final String DATA_SEPARATOR = "&";

    private QueryStringUtil() {
    }

    public static Map<String, String> parse(final String queryString) {
        Map<String, String> data = new HashMap<>();
        final String[] split = queryString.split(DATA_SEPARATOR);
        for (String each : split) {
            final String[] keyAndValue = each.split("=");
            data.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return data;
    }
}
