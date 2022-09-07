package nextstep.jwp;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageConverter {

    public static final String SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    public static Map<String, String> convert(final String body) {
        return Arrays.stream(body.split(SEPARATOR))
                .map(it -> it.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(it -> it[KEY_INDEX], it -> it[VALUE_INDEX]));
    }
}
