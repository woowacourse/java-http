package nextstep.jwp.controller;

import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.request.HttpRequest;

public interface Handler {

    boolean matchHttpMethod(HttpMethod httpMethod);

    String handle(HttpRequest httpRequest, Controller controller) throws Exception;
}
