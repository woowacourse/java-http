package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class HttpXW3UrlEncodedBody implements HttpBody {

    private static final String PAIR_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";

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

        for (String pair : pairs) {
            if (pair.contains(PAIR_DELIMITER)) {
                String[] keyValuePair = pair.split(PAIR_DELIMITER);
                putIfValidPair(keyValuePair);
            }
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            return;
        }
        String key = keyValuePair[0];
        String value = keyValuePair[1];

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
