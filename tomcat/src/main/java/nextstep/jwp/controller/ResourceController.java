package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;

import nextstep.jwp.util.ResourceLoaderUtil;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.http11.handler.AbstractController;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = ResourceLoaderUtil.loadContent(request.requestUrl());
        response.setVersion(request.protocolVersion());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, ContentType.negotiate(request.requestUrl()));
        response.addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        response.setBody(content);
    }
}
