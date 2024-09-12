package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public HttpResponse dispatch(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        String path = request.getPath();
        if (FileExtension.isFileExtension(path)) {
            new StaticResourceHandler().handle(request, response);
            return response;
        }
        Controller controller = getController(path);
        controller.service(request, response);
        return response;
    }

    private Controller getController(String path) {
        Controller controller = controllers.get(path);
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
