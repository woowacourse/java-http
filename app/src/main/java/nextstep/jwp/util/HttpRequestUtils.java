package nextstep.jwp.util;

import com.google.common.collect.Maps;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestUtils {

    public static final int PAIR_SIZE = 2;

    private HttpRequestUtils() {
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (StringUtils.isEmptyOrWhitespace(queryString)) {
            return Maps.newHashMap();
        }

        String[] elements = queryString.split("&");
        return Arrays.stream(elements)
                .map(HttpRequestUtils::split)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> split(String element) {
        String[] pair = element.split("=");

        if (pair.length != PAIR_SIZE) {
            return null;
        }

        return Map.entry(pair[0], pair[1]);
    }
}
