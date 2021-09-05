package nextstep.jwp.http.request;

import nextstep.jwp.WebServer;
import nextstep.jwp.http.common.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private HttpMethod httpMethod;
    private URI uri;
    private String versionOfProtocol;

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private RequestLine(HttpMethod httpMethod, URI uri, String versionOfProtocol) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.versionOfProtocol = versionOfProtocol;
    }

    public static RequestLine of(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        if (splitRequestLine.length != 3) {
            for (String s : splitRequestLine) {
                logger.debug("스플릿 : {}", s);
            }
            throw new IllegalArgumentException("잘못된 RequestLine 입니다.");
        }
        return new RequestLine(
                HttpMethod.valueOf(splitRequestLine[0]),
                URI.of(splitRequestLine[1]),
                splitRequestLine[2]
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getQueryParameter(String key) {
        return uri.getQueryParameter(key);
    }
}
