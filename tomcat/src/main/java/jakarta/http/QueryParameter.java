package jakarta.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParameter {

    private static final String PAIR_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";
    private static final int KEY_PAIR_LENGTH = 2;
    private static final int KEY_POSITION = 0;
    private static final int VALUE_POSITION = 1;

    private final Map<String, String> queryParameter = new HashMap<>();

    public QueryParameter(String queryParameter) {
        if (queryParameter == null) {
            queryParameter = "";
        }

        parseQueryParameter(queryParameter);
    }

    private void parseQueryParameter(String queryParameter) {
        String[] pairs = queryParameter.split(PARAMETER_DELIMITER);

        Arrays.stream(pairs).forEach(this::parsePair);
    }

    private void parsePair(String pair) {
        if (pair.contains(PAIR_DELIMITER)) {
            String[] keyValuePair = pair.split(PAIR_DELIMITER);
            putIfValidPair(keyValuePair);
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != KEY_PAIR_LENGTH) {
            return;
        }
        String key = keyValuePair[KEY_POSITION];
        String value = keyValuePair[VALUE_POSITION];

        queryParameter.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(queryParameter.get(key));
    }

    public boolean isEmpty() {
        return queryParameter.isEmpty();
    }

    public int getSize() {
        return queryParameter.size();
    }
}
