package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;

public interface Controller {

    ModelView process(HttpRequest httpRequest, HttpResponse httpResponse);
}
