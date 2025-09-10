package org.apache.catalina.controller;

import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final RequestMapping requestMapping;
    private final AbstractController staticResourceController;

    public FrontController() {
        this.requestMapping = new RequestMapping();
        this.staticResourceController = new StaticResourceController();
        initializeControllers();
    }

    private void initializeControllers() {
        requestMapping.addController("/", new HelloController());
        requestMapping.addController("/login", new LoginController());
        requestMapping.addController("/register", new RegisterController());
    }

    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        Controller controller = requestMapping.getController(path);
        if (controller == null) {
            staticResourceController.service(request, response);
            return;
        }
        controller.service(request, response);
    }
}

