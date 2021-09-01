package nextstep.jwp.controller;

import nextstep.jwp.constants.HttpMethod;

public interface Handler {

    boolean matchHttpMethod(HttpMethod httpMethod);

    String runController(String uri, Controller controller) throws Exception;
}
