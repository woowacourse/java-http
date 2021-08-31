package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.GeneralResponse;

public class ExtraController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        GeneralResponse generalResponse = new GeneralResponse(request);
        response.setResponse(generalResponse.getResponse());
    }
}
