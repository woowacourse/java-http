package servlet.http.request.uri;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class QueryParams {

    private static final QueryParams EMPTY = new QueryParams(Collections.emptyMap());
    private static final String PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int UNLIMITED_SPLIT = -1;
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams;

    private QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    protected static QueryParams from(String queryParams) {
        if (isEmptyQueryParams(queryParams)) {
            return EMPTY;
        }
        return Arrays.stream(queryParams.split(PARAMS_DELIMITER))
                .map(QueryParams::splitKeyValue)
                .collect(collectingAndThen(
                        toMap(p -> p[KEY_INDEX].strip(), p -> p[VALUE_INDEX].strip()), QueryParams::new)
                );
    }

    private static boolean isEmptyQueryParams(String queryParams) {
        return queryParams == null || queryParams.isBlank();
    }

    private static String[] splitKeyValue(String queryParam) {
        String[] keyValue = queryParam.split(KEY_VALUE_DELIMITER, UNLIMITED_SPLIT);
        if (keyValue.length != VALID_KEY_VALUE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Query parameter입니다. query parameter: '%s'".formatted(queryParam));
        }
        validateNotBlankKeyValue(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        return keyValue;
    }

    private static void validateNotBlankKeyValue(String key, String value) {
        if (key.isBlank() || value.isBlank()) {
            throw new IllegalArgumentException("key 또는 value가 비어있습니다. key: '%s', value: '%s'".formatted(key, value));
        }
    }

    protected String get(String key) {
        validateNotEmptyParams();
        if (!queryParams.containsKey(key)) {
            throw new IllegalArgumentException("Query parameter에 해당 key가 존재하지 않습니다. key: %s".formatted(key));
        }
        return queryParams.get(key);
    }

    private void validateNotEmptyParams() {
        if (!existQueryParams()) {
            throw new IllegalArgumentException("Query parameter가 비어있습니다.");
        }
    }

    protected boolean existQueryParams() {
        return !queryParams.isEmpty();
    }
}
