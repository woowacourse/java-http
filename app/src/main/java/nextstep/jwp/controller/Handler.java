package nextstep.jwp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.request.HttpRequest;

public interface Handler {

    boolean matchHttpMethod(HttpMethod httpMethod);

    String handle(HttpRequest httpRequest, Controller controller)
            throws InvocationTargetException, IllegalAccessException, IOException;
}
