package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.HttpMethod;
import nextstep.jwp.httpmessage.HttpRequest;
import nextstep.jwp.httpmessage.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public ModelView process(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            return doGet(httpRequest, httpResponse);
        }
        return doPost(httpRequest, httpResponse);
    }

    protected abstract ModelView doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract ModelView doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
