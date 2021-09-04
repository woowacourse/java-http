package nextstep.jwp.framework.http.common;

import static nextstep.jwp.framework.http.common.HttpHeaders.HEADER_DELIMITER;
import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class QueryParams {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public QueryParams() {
        this.value = new HashMap<>();
    }

    public QueryParams(String query) {
        this.value = createQueryParams(query);
    }

    private Map<String, String> createQueryParams(String query) {
        final Map<String, String> queryParams = new HashMap<>();
        final String[] params = query.split("&");

        for (String param : params) {
            final String[] element = param.split("=", 2);
            queryParams.put(element[KEY_INDEX].trim(), element[VALUE_INDEX].trim());
        }

        return queryParams;
    }

    public int count() {
        return value.size();
    }

    public Map<String, String> getValue() {
        return Collections.unmodifiableMap(value);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (Entry<String, String> pair : value.entrySet()) {
            builder.append(pair.getKey())
                .append(HEADER_DELIMITER)
                .append(pair.getValue())
                .append(LINE_DELIMITER);
        }

        return builder.toString();
    }
}
