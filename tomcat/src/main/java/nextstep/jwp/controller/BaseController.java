package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class BaseController extends AbstractController {

    ResponseHelper responseHelper = new ResponseHelper();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        responseHelper.loadRawString(response, "Hello world!");
    }
}
