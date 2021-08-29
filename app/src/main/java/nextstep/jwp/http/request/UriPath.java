package nextstep.jwp.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UriPath {

    private static final Logger LOG = LoggerFactory.getLogger(UriPath.class);

    private final String uri;
    private final Map<String, String> queryParams;

    public UriPath(String path) {
        queryParams = new ConcurrentHashMap<>();
        final int delimiterIndex = path.indexOf("?");
        if (delimiterIndex != -1) {
            LOG.debug("delimiterIndex : {}", delimiterIndex);
            uri = path.substring(0, delimiterIndex);
            LOG.debug("uri : {}", uri);
            final String queryParam = path.substring(delimiterIndex + 1);
            LOG.debug("queryParams : {}", queryParam);
            parseQueryParams(queryParam);
            return;
        }
        uri = path;
    }

    private void parseQueryParams(String params) {
        for (String param : params.split("&")) {
            parseSingleParam(param);
        }
    }

    private void parseSingleParam(String param) {
        final String[] splitParam = param.split("=");
        final String key = splitParam[0];
        final String value = splitParam[1];
        LOG.debug("URI path query params => key: {}, value: {}", key, value);
        queryParams.put(key, value);
    }

    public String getUri() {
        return uri;
    }
}
