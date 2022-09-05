package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.util.ResourceUtil;

public class ResourceHandler implements Controller {

    @Override
    public String service(final HttpRequest httpRequest) throws IOException {
        return ResourceUtil.getResource(httpRequest.getRequestUri());
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
