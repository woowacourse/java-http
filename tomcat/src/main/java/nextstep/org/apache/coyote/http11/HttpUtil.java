package nextstep.org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpUtil {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX = 0;

    private HttpUtil() {
    }

    public static void parseMultipleValues(
            Map<String, String> parsedValues,
            String multipleValues,
            String valuesDelimiter, String keyValueDelimiter
    ) {
        Arrays.asList(multipleValues.split(valuesDelimiter)).forEach(header -> {
            String[] splited = header.split(keyValueDelimiter);
            parsedValues.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
        });
    }

    public static String selectFirstContentTypeOrDefault(String acceptHeader) {
        if (Objects.isNull(acceptHeader)) {
            return "text/html";
        }
        List<String> acceptHeaderValues = Arrays.asList(acceptHeader.split(","));
        return acceptHeaderValues.get(ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX);
    }
}
