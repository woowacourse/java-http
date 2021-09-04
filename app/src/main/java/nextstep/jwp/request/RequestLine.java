package nextstep.jwp.request;

import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.exception.HttpException;

public class RequestLine {
    private static final int REQUEST_LINE_SIZE = 3;
    private static final int METHOD = 0;
    private static final int URI = 1;

    private final String httpMethod;
    private final String uri;

    public RequestLine(String requestLine) {
        String[] requests = splitRequestLine(requestLine);
        this.httpMethod = requests[METHOD];
        this.uri = requests[URI];
    }

    private String[] splitRequestLine(String requestLine) {
        String[] requests = requestLine.split(HttpTerms.EMPTY_SPACE_SEPARATOR);
        if (requests.length < REQUEST_LINE_SIZE) {
            throw new HttpException("올바르지 않은 http 요청이 들어왔습니다.");
        }
        return requests;
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.findHttpMethod(httpMethod);
    }

    public String getUri() {
        return this.uri;
    }
}
