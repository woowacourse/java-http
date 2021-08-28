package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class URI {

    private static final String QUERY_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private static final String VALUE_DELIMITER = ",";

    private final String baseUri;
    private final Map<String, List<String>> query;

    private URI(final String baseUri, final Map<String, List<String>> query) {
        this.baseUri = baseUri;
        this.query = query;
    }

    public URI(final String baseUri) {
        this(baseUri, new HashMap<>());
    }

    public static URI of(final String uri) {
        final List<String> splitUri = Arrays.asList(uri.split(QUERY_DELIMITER));

        if (hasNotQuery(splitUri)) {
            return new URI(splitUri.get(0));
        }
        return new URI(splitUri.get(0), splitQueryParameter(splitUri.get(1)));
    }

    private static boolean hasNotQuery(final List<String> splitUri) {
        return splitUri.size() == 1;
    }

    private static Map<String, List<String>> splitQueryParameter(final String query) {
        final Map<String, List<String>> result = new HashMap<>();

        for (final String parameter : query.split(QUERY_PARAMETER_DELIMITER)) {
            final List<String> keyAndValue = Arrays.asList(parameter.split(KEY_AND_VALUE_DELIMITER));
            validateKeyAndValue(keyAndValue);
            result.put(keyAndValue.get(0), Arrays.asList(keyAndValue.get(1).split(VALUE_DELIMITER)));
        }

        return result;
    }

    private static void validateKeyAndValue(final List<String> keyAndValue) {
        if (keyAndValue.size() != 2) {
            throw new IllegalArgumentException("Invalid query parameter");
        }
    }

    public String getBaseUri() {
        return baseUri;
    }

    public boolean hasKeys(final List<String> keys) {
        return keys.stream().allMatch(query::containsKey);
    }

    public String getValue(final String key) {
        if (!query.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Cannot find parameter(%s).", key));
        }

        return query.get(key).get(0);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final URI uri = (URI) o;
        return Objects.equals(baseUri, uri.baseUri) && Objects.equals(query, uri.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUri, query);
    }
}
