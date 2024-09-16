package jakarta.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class HttpXW3UrlEncodedBody implements HttpBody {

    private static final String PAIR_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";
    private static final int KEY_PAIR_LENGTH = 2;
    private static final int KEY_POSITION = 0;
    private static final int VALUE_POSITION = 1;

    private final Map<String, String> pairs = new HashMap<>();

    public HttpXW3UrlEncodedBody(char[] body) {
        String bodyToken = "";
        if (body != null) {
            bodyToken = new String(body);
        }

        parseBody(bodyToken);
    }

    private void parseBody(String bodyToken) {
        String[] pairs = bodyToken.split(PARAMETER_DELIMITER);

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

        pairs.put(key, value);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(pairs.get(key));
    }

    @Override
    public boolean isEmpty() {
        return pairs.isEmpty();
    }

    @Override
    public int getSize() {
        return pairs.size();
    }
}
