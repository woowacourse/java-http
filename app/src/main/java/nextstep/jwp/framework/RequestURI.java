package nextstep.jwp.framework;

import static nextstep.jwp.framework.RequestHeader.DELIMITER;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestURI {

    private static final String QUERY_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_REGEX = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_REGEX = "=";
    private static final String RESOURCE_PATH = "static/";
    private static final String PARSING = ".html";
    private static final int QUERY_CONTAINS_COUNT = 2;
    private static final int HTTP_PAGE_INDEX = 1;
    private static final int QUERY_PARAMETER_INDEX = 1;

    private final RequestURIWithQuery requestURIWithQuery;
    private final String url;
    private final URL resource;

    public RequestURI(final String line) {
        requestURIWithQuery = page(line);
        url = formatURL();
        resource = findResourceURL();
    }

    private RequestURIWithQuery page(final String line) {
        final String URILine = line.split(DELIMITER)[HTTP_PAGE_INDEX];

        if (URILine.split(QUERY_DELIMITER).length == QUERY_CONTAINS_COUNT) {
            final String queryParameterLine = URILine.split(QUERY_DELIMITER)[QUERY_PARAMETER_INDEX];
            final String[] splitQueryParameter = queryParameterLine.split(QUERY_PARAMETER_REGEX);
            final Map<String, String> queryParams = new HashMap<>();

            createQueryParam(splitQueryParameter, queryParams);

            return new RequestURIWithQuery(URILine, queryParams);
        }

        return new RequestURIWithQuery(URILine);
    }

    private void createQueryParam(final String[] splitQueryParameter, final Map<String, String> queryParams) {
        for (String queryParam : splitQueryParameter) {
            String[] split = queryParam.split(QUERY_PARAMETER_KEY_VALUE_REGEX);
            queryParams.put(split[0], split[1]);
        }
    }

    private String formatURL() {
        final URL resource = getClass().getClassLoader().getResource(RESOURCE_PATH + requestURIWithQuery.url());

        if (Objects.isNull(resource)) {
            return requestURIWithQuery.url() + PARSING;
        }

        return requestURIWithQuery.url();
    }

    private URL findResourceURL() {
        return getClass().getClassLoader().getResource(RESOURCE_PATH + url);
    }

    public RequestURIWithQuery getRequestURIWithQuery() {
        return requestURIWithQuery;
    }

    public String uri() {
        return requestURIWithQuery.getUri();
    }

    public Map<String, String> queryParam() {
        return requestURIWithQuery.getQueryParams();
    }

    public String getUrl() {
        return url;
    }

    public URL getResource() {
        return resource;
    }
}
