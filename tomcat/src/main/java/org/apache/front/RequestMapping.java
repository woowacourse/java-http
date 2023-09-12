package org.apache.front;

import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.exception.PageRedirectException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping implements Controller {

    private static final Map<String, AbstractController> urlMapper = new ConcurrentHashMap<>();

    static {
        urlMapper.put("/", new HelloWorldController());
        urlMapper.put("/login", new LoginController());
        urlMapper.put("/register", new RegisterController());
    }

    @Override
    public void service(final HttpRequest httpRequest, HttpResponse httpResponse) {
        if (!urlMapper.containsKey(httpRequest.getPath())) {
            throw new PageRedirectException.PageNotFound(httpResponse);
        }
        final AbstractController controller = urlMapper.get(httpRequest.getPath());
        controller.service(httpRequest, httpResponse);
    }
}
