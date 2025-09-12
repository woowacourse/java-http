package org.apache.catalina.router;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.loader.ResourceLoader;

public class FrontControllerFactory {

    private static final FrontController FRONT_CONTROLLER = create();

    private static FrontController create() {
        final ResourceLoader resourceLoader = new ResourceLoader();
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.addMapping("", new StaticResourceController(resourceLoader));
        requestMapping.addMapping("/", new DefaultController(resourceLoader));
        requestMapping.addMapping("/login", new LoginController(resourceLoader));
        requestMapping.addMapping("/register", new RegisterController(resourceLoader));
        return new FrontController(requestMapping);
    }

    public static FrontController getInstance() {
        return FRONT_CONTROLLER;
    }
}
