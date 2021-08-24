package nextstep.jwp.http.request.request_line;

public class RequestLine {

    private final String httpMethod;
    private final String path;
    private final String version;

    public RequestLine(String requestLine) {
        String[] parameters = requestLine.split(" ");

        this.httpMethod = parameters[0];
        this.path = parameters[1];
        this.version = parameters[2];
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
