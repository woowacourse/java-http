package nextstep.jwp.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLine.class);

    private final HttpMethod httpMethod;
    private final String url;
    private final String httpVersion;

    public RequestLine(final String requestLine) throws IOException {
        if (requestLine == null) {
            throw new IOException("requestLine이 비어있습니다.");
        }
        LOGGER.debug("requestLine : {}", requestLine);

        List<String> requestLineTokens = Arrays.asList(requestLine.split(" "));

        httpMethod = HttpMethod.valueOf(requestLineTokens.get(0));
        LOGGER.debug("httpMethod : {}", httpMethod);

        url = requestLineTokens.get(1);
        LOGGER.debug("url : {}", url);

        httpVersion = requestLineTokens.get(2);
        LOGGER.debug("httpVersion : {}", httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
