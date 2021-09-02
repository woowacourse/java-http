package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;

public class RedirectController implements Controller {
    @Override
    public ModelView process(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpStatusCode(HttpStatusCode.FOUND);
        return new ModelView("/404.html");
    }
}
