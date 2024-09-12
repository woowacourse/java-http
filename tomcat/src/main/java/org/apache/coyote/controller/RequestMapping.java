package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.LoginPageController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RegisterPageController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestKey;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;

public class RequestMapping {

    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put(RequestKey.ofGet("/login"), new LoginPageController());
        controllers.put(RequestKey.ofPost("/login"), new LoginController());
        controllers.put(RequestKey.ofGet("/register"), new RegisterPageController());
        controllers.put(RequestKey.ofPost("/register"), new RegisterController());
    }

    public HttpResponse dispatch(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        String path = request.getPath();
        if (FileExtension.isFileExtension(path)) {
            new StaticResourceHandler().handle(request, response);
            return response;
        }
        Controller controller = getController(request.getMethod(), path);
        controller.service(request, response);
        return response;
    }

    private Controller getController(HttpMethod method, String path) {
        Controller controller = controllers.get(new RequestKey(method, path));
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
