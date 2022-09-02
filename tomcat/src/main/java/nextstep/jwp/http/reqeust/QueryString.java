package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private Map<String, String> queryString = new HashMap<>();

    public QueryString(final String url) {
        if (url.contains("?")) {
            int index = url.indexOf("?");
            String[] queryStrings = url.substring(index + 1).split("&");

            for (String queryString : queryStrings) {
                String[] query = queryString.split("=");
                this.queryString.put(query[0], query[1]);
            }
        }
    }

    public boolean isNotEmpty() {
        return !queryString.isEmpty();
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
