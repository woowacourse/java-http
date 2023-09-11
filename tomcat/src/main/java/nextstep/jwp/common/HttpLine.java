package nextstep.jwp.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpLine {
    private final HttpMethod httpMethod;
    private final String url;
    private final String version;

    public HttpLine(HttpMethod httpMethod, String url, String version) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.version = version;
    }

    public static HttpLine from(String httpLine) {
        List<String> values = Arrays.stream(httpLine.split(" ")).collect(Collectors.toList());
        return new HttpLine(HttpMethod.from(values.get(0)), values.get(1), values.get(2));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
