package nextstep.jwp.controller;

import java.io.IOException;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

import static nextstep.jwp.PageUrl.INDEX_PAGE;

public class ResourceController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(getPath(request));
    }

    private String getPath(HttpRequest httpRequest) {
        if ("/".equals(httpRequest.getPath())) {
            return INDEX_PAGE.getPath();
        }
        return httpRequest.getPath();
    }
}
