package nextstep.jwp.controller;

import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.request.RequestHeader;

public interface Handler {

    boolean matchHttpMethod(HttpMethod httpMethod);

    String runController(String uri, RequestHeader requestHeader, Controller controller) throws Exception;
}
