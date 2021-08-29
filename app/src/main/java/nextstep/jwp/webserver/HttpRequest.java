package nextstep.jwp.webserver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequest {

    private HttpHeaders httpHeaders;
    private HttpMethod httpMethod;
    private QueryParams queryParams;
    private String body;
    private String uri;

    public HttpRequest(String requestString) {
        parseRequest(requestString);
    }

    private void parseRequest(String request) {
        String[] headerBody = request.split("\n\n");
        String header = headerBody[0];

        List<String> headerLines = header.lines().collect(Collectors.toList());

        String requestLine = headerLines.get(0);
        parseRequestLine(requestLine);
        parseHeaders(headerLines);
        parseBody(headerBody);
    }

    private void parseRequestLine(String requestLine) {
        List<String> tokens = Arrays.asList(requestLine.split(" "));
        httpMethod = HttpMethod.from(tokens.get(0));
        parsePath(tokens.get(1));
    }

    private void parsePath(String uriPath) {
        String[] splitUri = uriPath.split("\\?");
        uri = splitUri[0];

        parseQueryParams(splitUri);
    }

    private void parseQueryParams(String[] splitUri) {
        if (splitUri.length > 1) {
            queryParams = new QueryParams(splitUri[1]);
        }
    }

    private void parseHeaders(List<String> headerLines) {
        httpHeaders = new HttpHeaders(
                headerLines.subList(1, headerLines.size() - 1));
    }

    private void parseBody(String[] headerBody) {
        if (headerBody.length > 1) {
            this.body = headerBody[1];
        }
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

    public String getBody() {
        return body;
    }
}
