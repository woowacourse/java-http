package nextstep.jwp.framework.http;

import java.util.Map;
import java.util.Objects;

import nextstep.jwp.framework.util.StringUtils;

public class URI {

    private final String path;
    private final Query query;

    public URI(String uri) {
        this(URI.Parser.parse(uri));
    }

    public URI(URI uri) {
        this(uri.path, uri.query);
    }

    public URI(String path, Query query) {
        this.path = StringUtils.requireNonBlank(path);
        this.query = Objects.requireNonNull(query);
    }

    public String getPath() {
        return path;
    }

    public boolean isSamePath(String path) {
        return this.path.equals(path);
    }

    public Map<String, String> getQueries() {
        return query.getQueries();
    }

    private static class Parser {
        public static URI parse(String uri) {
            int index = uri.indexOf("?");
            if (hasPathOnly(index)) {
                return new URI(uri, new Query());
            }

            String path = uri.substring(0, index);
            String query = uri.substring(index + 1);

            return new URI(path, new Query(query));
        }

        private static boolean hasPathOnly(int index) {
            return index == -1;
        }
    }
}
