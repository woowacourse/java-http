package nextstep.jwp.controller;

import nextstep.jwp.domain.request.HttpRequest;

public class RequestMapper {

    private RequestMapper() {
        throw new IllegalStateException("Utility Class");
    }

    public static Controller getController(HttpRequest httpRequest) {
        return ControllerManager.findControllerByUri(httpRequest.getUri());
    }
}
