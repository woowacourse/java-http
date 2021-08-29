package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.Query;
import nextstep.jwp.exception.HttpException;

public class RequestLine {
    public static final int REQUEST_LINE_SIZE = 3;
    public static final int METHOD = 0;
    public static final int URI = 1;

    private final String httpMethod;
    private final String uri;
    private final Map<String, String> params;

    public RequestLine(String requestLine) {
        String[] requests = splitRequestLine(requestLine);
        this.httpMethod = requests[METHOD];
        this.uri = requests[URI];
        this.params = new HashMap<>();
    }

    private String[] splitRequestLine(String requestLine) {
        String[] requests = requestLine.split(Http.SEPARATOR);
        if (requests.length < REQUEST_LINE_SIZE) {
            throw new HttpException("올바르지 않은 http 요청이 들어왔습니다.");
        }
        return requests;
    }

    public Boolean isQueryString() {
        if (uri.contains(Query.QUESTION)) {
            extractQueryString(uri);
            return true;
        }
        return false;
    }

    private Map<String, String> extractQueryString(String uri) {
        int index = uri.indexOf(Query.QUESTION);
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split(Query.SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(Query.EQUAL);
            this.params.put(split[Query.KEY], split[Query.VALUE]);
        }
        return this.params;
    }

    public String getUri() {
        return this.uri;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }
}
