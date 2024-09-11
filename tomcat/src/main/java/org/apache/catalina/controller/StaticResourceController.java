package org.apache.catalina.controller;

import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("Requested File Path: {}", request.getPath());
        ResponseFile file = StaticResourceManager.read(request.getPath());
        response.setStatus(HttpStatusCode.OK)
                .setBody(file);
    }
}
