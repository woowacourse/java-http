package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ContentType;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ResourceRequestHandler.class);
    private static final String RESOURCE_DELIMITER = "\\.";

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - ResourceRequestHandler");
        String contentType = ContentType.findContentType(
                request.getUri().split(RESOURCE_DELIMITER)[1]
        );

        String resource = ResourceFinder.resource(request.getUri());
        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        response.setHeaders(headers(resource.getBytes().length, contentType));
        response.setResponseBody(resource);
    }

    private Map<String, String> headers(int resourceLength, String contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", resourceLength + "");

        return headers;
    }
}
