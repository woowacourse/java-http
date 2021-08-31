package nextstep.jwp.http.message;

import java.util.Optional;
import nextstep.jwp.exception.HttpRequestFormatException;

public class HttpRequestLine {

    private String value;

    private HttpMethod method;

    private String uri;

    private String versionOfProtocol;

    private HttpRequestLine(String value) {
        parse(value);
        this.value = value;
    }

    public static HttpRequestLine createByString(String value) {
        return new HttpRequestLine(value);
    }

    private void parse(String requestLine) {
        String[] splittedRequestLine = requestLine.split(" ");
        if (splittedRequestLine.length != 3) {
            throw new HttpRequestFormatException();
        }
        method = HttpMethod.of(splittedRequestLine[0]);
        uri = splittedRequestLine[1];
        versionOfProtocol = splittedRequestLine[2];
    }

    public String asString() {
        return value;
    }

    public Optional<String> getQueryString() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }
        String queryString = uri.substring(index + 1);
        return Optional.ofNullable(queryString);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Optional<String> getUri() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Optional.ofNullable(uri);
        }
        String path = uri.substring(0, index);
        return Optional.ofNullable(path);
    }

    public Optional<String> getVersionOfProtocol() {
        return Optional.ofNullable(versionOfProtocol);
    }
}
