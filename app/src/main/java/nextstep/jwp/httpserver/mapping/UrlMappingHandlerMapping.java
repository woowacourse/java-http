package nextstep.jwp.httpserver.mapping;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.httpserver.domain.HttpMethod;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public class UrlMappingHandlerMapping implements HandlerMapping {

    private final Map<String, Object> urlMappings = new HashMap<>();

    public UrlMappingHandlerMapping(Map<String, Object> urlMappings) {
        this.urlMappings.putAll(urlMappings);
    }

    @Override
    public boolean canUse(HttpRequest httpRequest) {
        final String requestUri = httpRequest.getRequestUri();
        return urlMappings.containsKey(requestUri);
    }

    @Override
    public Object getHandler(HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();

        if (HttpMethod.GET == httpRequest.getHttpMethod()) {
            requestUri = removeQueryString(requestUri);
        }

        return urlMappings.get(requestUri);
    }

    private String removeQueryString(String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            requestUri = requestUri.substring(0, index);
        }
        return requestUri;
    }
}
