package nextstep.jwp.controller;

import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.HttpRequest;
import nextstep.jwp.httpmessage.HttpResponse;

public interface Controller {

    ModelView process(HttpRequest httpRequest, HttpResponse httpResponse);
}
