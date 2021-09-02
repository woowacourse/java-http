package nextstep.jwp.util;

import com.google.common.collect.Maps;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestUtils {

    public static final String AND_DELIMITER = "&";
    public static final String EQUALS_DELIMITER = "=";
    public static final int PAIR_SIZE = 2;

    private HttpRequestUtils() {
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (StringUtils.isEmptyOrWhitespace(queryString)) {
            return Maps.newHashMap();
        }

        String[] elements = queryString.split(AND_DELIMITER);
        return Arrays.stream(elements)
                .map(HttpRequestUtils::splitByEqual)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map.Entry<String, String> splitByEqual(String element) {
        String[] pair = element.split(EQUALS_DELIMITER);

        if (pair.length != PAIR_SIZE) {
            return null;
        }

        return Map.entry(pair[0], pair[1]);
    }
}
