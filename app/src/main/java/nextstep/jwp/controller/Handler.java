package nextstep.jwp.controller;

import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestHeader;

public interface Handler {

    boolean matchHttpMethod(HttpMethod httpMethod);

    String handle(HttpRequest httpRequest, Controller controller) throws Exception;
}
