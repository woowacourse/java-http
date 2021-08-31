package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.constants.Http;
import nextstep.jwp.exception.HttpException;

public class RequestLine {
    private static final int REQUEST_LINE_SIZE = 3;
    private static final int METHOD = 0;
    private static final int URI = 1;

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

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getUri() {
        return this.uri;
    }
}
