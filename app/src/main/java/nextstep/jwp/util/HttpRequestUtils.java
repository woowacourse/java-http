package nextstep.jwp.util;

import com.google.common.collect.Maps;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestUtils {

    public static final int ENTRY_SIZE = 2;

    private HttpRequestUtils() {
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (StringUtils.isEmptyOrWhitespace(queryString)) {
            return Maps.newHashMap();
        }

        String[] entries = queryString.split("&");
        return Arrays.stream(entries)
                .map(HttpRequestUtils::splitKeyAndValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> splitKeyAndValue(String entry) {
        String[] splitEntry = entry.split("=");

        if (splitEntry.length != ENTRY_SIZE) {
            return null;
        }

        return Map.entry(splitEntry[0], splitEntry[1]);
    }
}
