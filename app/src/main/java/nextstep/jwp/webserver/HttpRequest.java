package nextstep.jwp.webserver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequest {

    private HttpHeaders httpHeaders;
    private HttpMethod httpMethod;
    private QueryParams queryParams = new QueryParams();
    private FormBody body = FormBody.emptyBody();
    private String uri;

    public HttpRequest(String requestString) {
        parseRequest(requestString);
    }

    private void parseRequest(String request) {
        String[] headerBody = request.split("\r\n\r\n");
        String header = headerBody[0];

        List<String> headerLines = header.lines().collect(Collectors.toList());

        String requestLine = headerLines.get(0);
        parseRequestLine(requestLine);
        initHeaders(headerLines);
        initBody(headerBody);
    }

    private void parseRequestLine(String requestLine) {
        List<String> tokens = Arrays.asList(requestLine.split(" "));
        httpMethod = HttpMethod.from(tokens.get(0));
        parsePath(tokens.get(1));
    }

    private void parsePath(String uriPath) {
        String[] splitUri = uriPath.split("\\?");
        uri = splitUri[0];

        initQueryParams(splitUri);
    }

    private void initQueryParams(String[] splitUri) {
        if (splitUri.length < 2) {
            queryParams = new QueryParams();
            return;
        }
        queryParams = new QueryParams(splitUri[1]);
    }

    private void initHeaders(List<String> headerLines) {
        httpHeaders = new HttpHeaders(
                headerLines.subList(1, headerLines.size()));
    }

    private void initBody(String[] headerBody) {
        if (headerBody.length < 2) {
            return;
        }
        String bodyString = headerBody[1];
        body = new FormBody(bodyString);
    }

    public String getUri() {
        return uri;
    }

    public String getHeader(String header) {
        return httpHeaders.get(header);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getQueryParam(String param) {
        return queryParams.get(param);
    }

    public String getBody(String name) {
        return body.get(name);
    }
}
