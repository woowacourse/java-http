package org.apache.coyote.controller;

import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.ResponseBody;
import org.apache.coyote.domain.response.statusline.ContentType;
import org.apache.coyote.domain.response.statusline.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticFileController.class);

    @Override
    void doGet(HttpRequest request, HttpResponse response) throws Exception {
        log.info("[StaticFileController] doGet - {}", request.getFilePath());
        response.responseLine(request.getHttpVersion(), HttpStatusCode.OK)
                .header(ContentType.from(request.getFilePath()))
                .responseBody(ResponseBody.from(request.getFilePath()));
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws Exception {
        log.info("[StaticFileController] doPost - {}", request.getFilePath());
        response.responseLine(request.getHttpVersion(), HttpStatusCode.OK)
                .header(ContentType.from(request.getFilePath()))
                .responseBody(ResponseBody.from(request.getFilePath()));
    }

    @Override
    public boolean handle(HttpRequest httpRequest) {
        return false;
    }
}
