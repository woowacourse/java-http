package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URI {

    private static final String EMPTY = "";
    private static final String EQUAL = "=";
    private static final String AND = "&";
    private static final String QUESTION_MARK = "?";

    private final String path;
    private final Map<String, String> queryString;

    private URI(String path, Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static URI of(String uri) {
        HashMap<String, String> queryString = new HashMap<>();
        int index = uri.indexOf(QUESTION_MARK);
        if (index < 0) {
            return new URI(uri, null);
        }
        String path = uri.substring(0, index);
        String queries = uri.substring(index + 1);
        if (!queries.isEmpty()) {
            String[] queryStrings = queries.split(AND);
            for (String query : queryStrings) {
                String[] values = query.split(EQUAL);
                queryString.put(values[0], Objects.requireNonNullElse(values[1], EMPTY));
            }
        }
        return new URI(path, queryString);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public String getQueryParameter(String key) {
        return queryString.get(key);
    }
}
