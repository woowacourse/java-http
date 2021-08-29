package nextstep.jwp.http.request;

import java.util.Map;

public class QueryStringTypeRequestBody extends QueryStrings implements RequestBody{

    public QueryStringTypeRequestBody() {
    }

    public QueryStringTypeRequestBody(Map<String, String> queryStrings) {
        super(queryStrings);
    }

    @Override
    public Map<String, String> getAllContents() {
        return super.getAllQueryStrings();
    }
}
