package org.apache.coyote.handler;

import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ResourceUtil;

public class ResourceHandler implements Controller {

    @Override
    public String service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Map<String, String> httpHeaders = httpRequest.getHttpHeaders();
        httpResponse.setContentType(ResourceUtil.getContentType(httpRequest.getRequestUri()));
        return ResourceUtil.getResource(httpRequest.getRequestUri());
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
