package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.LoginPageController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RegisterPageController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestKey;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapping {

    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);
    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put(RequestKey.ofGet("/login"), new LoginPageController());
        controllers.put(RequestKey.ofPost("/login"), new LoginController());
        controllers.put(RequestKey.ofGet("/register"), new RegisterPageController());
        controllers.put(RequestKey.ofPost("/register"), new RegisterController());
    }

    public HttpResponse dispatch(HttpRequest request) {
        log(request);
        HttpResponse response = new HttpResponse();
        String path = request.getPath();
        if (FileExtension.isFileExtension(path)) {
            new StaticResourceController().service(request, response);
            return response;
        }
        Controller controller = getController(request.getMethod(), path);
        controller.service(request, response);
        return response;
    }

    private void log(HttpRequest request) {
        log.info("method = {}, path = {}, url = {}", request.getMethod(), request.getPath(), request.getUrl());
        for (String key : request.getQueryParams().keySet()) {
            log.info("key = {}, value = {}", key, request.getQueryParams().get(key));
        }
        for (String key : request.getHeaders().getCookies().getCookies().keySet()) {
            log.info("COOKIE: key = {}, value = {}", key, request.getHeaders().getCookies().getCookies().get(key));
        }
        if (!request.isBodyEmpty()) {
            log.info("body = {}", request.getBody());
        }
    }

    private Controller getController(HttpMethod method, String path) {
        Controller controller = controllers.get(new RequestKey(method, path));
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
