package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;

public class URI {

    private static final String QUERY_DELIMITER = "\\?";
    private static final UrlEncodingMapper URL_ENCODING_MAPPER = new UrlEncodingMapper();

    private final String baseUri;
    private final Map<String, String> query;

    private URI(final String baseUri, final Map<String, String> query) {
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
        return new URI(splitUri.get(0), URL_ENCODING_MAPPER.parse(splitUri.get(1)));
    }

    private static boolean hasNotQuery(final List<String> splitUri) {
        return splitUri.size() == 1;
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

        return query.get(key);
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
