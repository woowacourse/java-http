package nextstep.jwp.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLine.class);

    private final HttpMethod httpMethod;
    private String url;
    private final String httpVersion;
    private final Map<String, String> queryParams = new HashMap<>();

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

        queryStringPhasing();

        httpVersion = requestLineTokens.get(2);
        LOGGER.debug("httpVersion : {}", httpVersion);
    }

    private void queryStringPhasing() {
        if (url.contains("?")) {
            int index = url.indexOf("?");

            String queryString = url.substring(index + 1);
            String[] parameters = queryString.split("&");

            for (String parameter : parameters) {
                String[] splitParameter = parameter.split("=");
                queryParams.put(splitParameter[0], splitParameter[1]);
            }
            this.url = url.substring(0, index);
        }
    }

    public String getQueryParam(final String key) {
        return queryParams.get(key);
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
