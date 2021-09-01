package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public class ResourceController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addStatus(HttpStatus.OK);
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
        response.addBody(request.uri());
    }
}
