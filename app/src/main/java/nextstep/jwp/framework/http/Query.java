package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Query {

    private final Map<String, String> queries;

    public Query() {
        this(new HashMap<>());
    }

    public Query(String queries) {
        this(Parser.parse(Objects.requireNonNull(queries)));
    }

    public Query(Map<String, String> queries) {
        this.queries = new HashMap<>(queries);
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String get(String key) {
        return queries.get(key);
    }

    private static class Parser {
        public static final int KEY_INDEX = 0;
        public static final int VALUE_INDEX = 1;

        public static Map<String, String> parse(String queries) {
            Map<String, String> queryMap = new HashMap<>();
            for (String query : separateQueries(queries)) {
                final String[] queryToken = query.split("=");
                if (isQueryForm(queryToken)) {
                    break;
                }

                final String key = queryToken[KEY_INDEX].trim();
                final String value = queryToken[VALUE_INDEX].trim();

                if (anyBlank(key, value)) {
                    throw new IllegalArgumentException("쿼리의 키 혹은 밸류가 빈 값입니다.");
                }

                queryMap.put(key, value);
            }
            return queryMap;
        }

        private static String[] separateQueries(String queries) {
            return queries.split("&");
        }

        private static boolean isQueryForm(String[] queryToken) {
            return queryToken.length == 1;
        }

        private static boolean anyBlank(String key, String value) {
            return key.isBlank() || value.isBlank();
        }
    }
}
