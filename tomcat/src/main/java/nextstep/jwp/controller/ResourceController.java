package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {

    ResponseHelper responseHelper = new ResponseHelper();
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        responseHelper.loadResource(response, request.getUrl());
    }
}
