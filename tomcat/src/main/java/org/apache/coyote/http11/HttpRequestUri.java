package org.apache.coyote.http11;

import com.techcourse.controller.ControllerMapping;
import com.techcourse.controller.dto.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestUri {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUri.class);

    private final PathInfo pathInfo;
    private final Map<String, String> queryParams;

    public HttpRequestUri(PathInfo pathInfo) {
        this(pathInfo, Collections.emptyMap());
    }

    public HttpRequestUri(PathInfo pathInfo, Map<String, String> queryParams) {
        this.pathInfo = pathInfo;
        this.queryParams = queryParams;
    }

    public void processQueryParams(HttpMethod method) {
        if (queryParams.isEmpty()) {
            return;
        }
        ControllerMapping controllerMapping = pathInfo.getControllerMapping(method);
        Response<?> response = controllerMapping.apply(queryParams);
        log.info("user : {}", response.response());
    }

    public HttpResponse getHttpResponse() throws IOException {
        return pathInfo.getHttpResponse();
    }
}
