package nextstep.jwp.framework.infrastructure.mapping;

import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.controller.Controller;

public interface RequestMapping {

    Controller findController(HttpRequest httpRequest);
}
