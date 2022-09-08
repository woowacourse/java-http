package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.util.ResourceUtil;

public class ResourceHandler implements Controller {

    @Override
    public String service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Map<String, String> httpHeaders = httpRequest.getHttpHeaders();
        try {
            httpResponse.setContentType(Files.probeContentType(ResourceUtil.getPath(httpRequest.getRequestUri())));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
        return ResourceUtil.getResource(httpRequest.getRequestUri());
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
