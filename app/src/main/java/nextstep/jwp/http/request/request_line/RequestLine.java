package nextstep.jwp.http.request.request_line;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import nextstep.jwp.http.HttpVersion;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final HttpPath path;
    private final HttpVersion version;

    public RequestLine(String httpRequest) {
        String requestLine = httpRequest.split(LINE_SEPARATOR)[0];
        String[] parameters = requestLine.split(" ");

        this.httpMethod = HttpMethod.from(parameters[0]);
        this.path = new HttpPath(parameters[1]);
        this.version = HttpVersion.from(parameters[2]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpPath getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", httpMethod.name(), path.getUri(), version.asString());
    }
}
